import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

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
  pageSize = 5;
  totalPages = 0;
  totalElements = 0;

  // Formulario: si editingCoffee tiene valor, es edicion; si es null, es creacion
  editingCoffee: Coffee | null = null;
  formCoffee: Partial<Coffee> = this.emptyForm();

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

  /** Array de paginas a mostrar como botones numericos (0, 1, 2, ...). */
  pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
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
      alert('El pais es obligatorio');
      return;
    }

    if (this.editingCoffee) {
      // Modo edicion: PUT
      this.http.put<Coffee>(`/coffees/${this.editingCoffee.id}`, this.formCoffee, { withCredentials: true }).subscribe({
        next: () => {
          this.cancelEdit();
          this.loadCoffees();
        },
        error: (e) => alert('Error al actualizar: ' + (e.error?.message || e.status))
      });
    } else {
      // Modo creacion: POST
      this.http.post<Coffee>('/coffees', this.formCoffee, { withCredentials: true }).subscribe({
        next: () => {
          this.formCoffee = this.emptyForm();
          this.loadCoffees();
        },
        error: (e) => alert('Error al crear: ' + (e.error?.message || e.status))
      });
    }
  }

  deleteCoffee(coffee: Coffee): void {
    if (!confirm(`Borrar el cafe de ${coffee.country} (${coffee.variety || 'sin variedad'})?`)) return;

    this.http.delete(`/coffees/${coffee.id}`, { withCredentials: true }).subscribe({
      next: () => this.loadCoffees(),
      error: (e) => alert('Error al borrar: ' + (e.error?.message || e.status))
    });
  }

  // --- Helpers ---
  private emptyForm(): Partial<Coffee> {
    return {
      country: '',
      variety: '',
      processingMethod: '',
      score: 85
    };
  }
}
