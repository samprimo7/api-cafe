import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Lang, TranslationKey, translate } from './translations';

// Modelos
interface User {
  login: string;
  name: string;
  avatarUrl: string;
  isAdmin: boolean;
}

interface Coffee {
  id: number;
  country: string;
  region: string;
  species: string;
  variety: string;
  processingMethod: string;
  harvestYear: string;
  altitudeMeanMeters: number;
  score: number;
  aroma: number;
  flavor: number;
  aftertaste: number;
  acidity: number;
  body: number;
  balance: number;
}

interface CoffeePage {
  content: Coffee[];
  totalElements: number;
  totalPages: number;
  number: number;     // pagina actual (0-indexed)
  first: boolean;
  last: boolean;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  private http = inject(HttpClient);

  user: User | null = null;
  coffees: Coffee[] = [];
  searchCountry = '';

  // Estado de paginacion
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  // Formulario: si editingCoffee tiene valor, es edicion; si es null, es creacion
  editingCoffee: Coffee | null = null;
  formCoffee: Partial<Coffee> = this.emptyForm();

  // Toast (notificacion flotante temporal)
  toast: { message: string; type: 'success' | 'error' } | null = null;

  // Cafe pendiente de confirmacion para borrar (null => modal cerrado)
  coffeeToDelete: Coffee | null = null;

  // i18n
  currentLang: Lang = (localStorage.getItem('lang') as Lang) || 'es';

  t(key: string, params?: Record<string, string | number>): string {
    return translate(this.currentLang, key as TranslationKey, params);
  }

  toggleLanguage(): void {
    this.currentLang = this.currentLang === 'es' ? 'en' : 'es';
    localStorage.setItem('lang', this.currentLang);
  }

  get groupedCoffees(): { method: string; coffees: Coffee[] }[] {
    const groups = new Map<string, Coffee[]>();
    for (const c of this.coffees) {
      const key = c.processingMethod || '—';
      if (!groups.has(key)) groups.set(key, []);
      groups.get(key)!.push(c);
    }
    return Array.from(groups.entries()).map(([method, coffees]) => ({ method, coffees }));
  }

  translateProcessing(method: string): string {
    const map: Record<string, string> = {
      'Washed / Wet':   this.t('proc_washed'),
      'Natural / Dry':  this.t('proc_natural'),
      'Pulped Natural': this.t('proc_pulped'),
      'Honey':          this.t('proc_honey'),
    };
    return map[method] ?? method;
  }

  ngOnInit(): void {
    this.loadUser();
    this.loadCoffees();
  }

  loadUser(): void {
    this.http.get<User>('/me', { withCredentials: true }).subscribe({
      next: (u) => this.user = u,
      error: () => this.user = null
    });
  }

  loadCoffees(): void {
    const params = new URLSearchParams();
    if (this.searchCountry) params.set('country', this.searchCountry);
    params.set('page', String(this.currentPage));
    params.set('size', String(this.pageSize));

    this.http.get<CoffeePage>(`/coffees/search?${params.toString()}`, { withCredentials: true }).subscribe({
      next: (page) => {
        this.coffees = page.content;
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
      },
      error: (e) => console.error('Error cargando cafes', e)
    });
  }

  // --- Paginacion ---
  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadCoffees();
  }

  nextPage(): void { this.goToPage(this.currentPage + 1); }
  prevPage(): void { this.goToPage(this.currentPage - 1); }

  /**
   * Devuelve los items a pintar en la barra de paginacion.
   * Numeros = botones de pagina. La cadena '...' = puntos suspensivos.
   *
   * Si hay 7 paginas o menos -> muestra todas.
   * Si hay mas -> muestra primera, ultima, current +/- 1, y "..." en los huecos.
   *
   * Ejemplos (15 paginas):
   *   current = 0  -> [0, 1, 2, 3, ..., 14]
   *   current = 7  -> [0, ..., 6, 7, 8, ..., 14]
   *   current = 14 -> [0, ..., 11, 12, 13, 14]
   */
  pageNumbers(): (number | '...')[] {
    const total = this.totalPages;
    const current = this.currentPage;

    // Pocas paginas -> mostramos todas, sin elipsis
    if (total <= 7) {
      return Array.from({ length: total }, (_, i) => i);
    }

    const result: (number | '...')[] = [];

    // Ventana de paginas alrededor de la actual
    let start = Math.max(1, current - 1);
    let end = Math.min(total - 2, current + 1);

    // Si estamos cerca del principio, ensanchamos a la derecha
    if (current <= 2) end = 3;
    // Si estamos cerca del final, ensanchamos a la izquierda
    if (current >= total - 3) start = total - 4;

    // Primera pagina siempre
    result.push(0);

    // "..." si hay hueco entre la primera y la ventana
    if (start > 1) result.push('...');

    // Las paginas intermedias
    for (let i = start; i <= end; i++) result.push(i);

    // "..." si hay hueco entre la ventana y la ultima
    if (end < total - 2) result.push('...');

    // Ultima pagina siempre
    result.push(total - 1);

    return result;
  }

  /** Reinicia paginacion cuando cambia la busqueda. */
  doSearch(): void {
    this.currentPage = 0;
    this.loadCoffees();
  }

  // --- Login / Logout ---
  login(): void {
    window.location.href = '/oauth2/authorization/github';
  }

  logout(): void {
    this.http.post('/logout', {}, { withCredentials: true }).subscribe({
      next: () => window.location.reload(),
      error: () => window.location.reload()
    });
  }

  // --- Crear / Editar / Borrar ---
  startCreate(): void {
    this.editingCoffee = null;
    this.formCoffee = this.emptyForm();
  }

  startEdit(coffee: Coffee): void {
    this.editingCoffee = coffee;
    this.formCoffee = { ...coffee }; // copia para no modificar el original hasta guardar
  }

  cancelEdit(): void {
    this.editingCoffee = null;
    this.formCoffee = this.emptyForm();
  }

  saveCoffee(): void {
    if (!this.formCoffee.country) {
      this.showToast(this.t('countryRequired'), 'error');
      return;
    }

    if (this.editingCoffee) {
      // Modo edicion: PUT
      this.http.put<Coffee>(`/coffees/${this.editingCoffee.id}`, this.formCoffee, { withCredentials: true }).subscribe({
        next: () => {
          this.showToast(this.t('coffeeUpdated'));
          this.cancelEdit();
          this.loadCoffees();
        },
        error: (e) => this.showToast(this.t('errorUpdate') + ': ' + (e.error?.message || e.status), 'error')
      });
    } else {
      // Modo creacion: POST
      this.http.post<Coffee>('/coffees', this.formCoffee, { withCredentials: true }).subscribe({
        next: () => {
          this.showToast(this.t('coffeeCreated'));
          this.formCoffee = this.emptyForm();
          this.loadCoffees();
        },
        error: (e) => this.showToast(this.t('errorCreate') + ': ' + (e.error?.message || e.status), 'error')
      });
    }
  }

  /** Abre el modal de confirmacion para borrar. */
  deleteCoffee(coffee: Coffee): void {
    this.coffeeToDelete = coffee;
  }

  /** Confirma el borrado: ejecuta el DELETE contra la API. */
  confirmDelete(): void {
    if (!this.coffeeToDelete) return;
    const id = this.coffeeToDelete.id;

    this.http.delete(`/coffees/${id}`, { withCredentials: true }).subscribe({
      next: () => {
        this.showToast(this.t('coffeeDeleted'));
        this.coffeeToDelete = null;
        this.loadCoffees();
      },
      error: (e) => {
        this.showToast(this.t('errorDelete') + ': ' + (e.error?.message || e.status), 'error');
        this.coffeeToDelete = null;
      }
    });
  }

  /** Cierra el modal sin borrar nada. */
  cancelDelete(): void {
    this.coffeeToDelete = null;
  }

  /**
   * Muestra una notificacion flotante temporal.
   * Se cierra sola a los 3 segundos.
   */
  showToast(message: string, type: 'success' | 'error' = 'success'): void {
    this.toast = { message, type };
    setTimeout(() => this.toast = null, 3000);
  }

  // --- Helpers ---
  private emptyForm(): Partial<Coffee> {
    return {
      country: '',
      region: '',
      variety: '',
      processingMethod: '',
      score: 85
    };
  }
}
