/**
 * Fichero de traducciones (analogo a un .po, pero en TypeScript).
 *
 * Para anadir un idioma nuevo, copia el bloque y traduce los valores.
 * Las cadenas pueden contener placeholders entre llaves: {nombre}
 */

export type Lang = 'es' | 'en';

const messages = {
  es: {
    // Header
    login: 'Iniciar sesión',
    loginTooltip: 'Iniciar sesión con GitHub',
    logoutTooltip: 'Cerrar sesión',
    languageTooltip: 'Cambiar idioma',
    roleAdmin: 'ADMIN',
    roleUser: 'USUARIO',

    // Search
    searchPlaceholder: 'Buscar por país (ej: eth)',
    searchTooltip: 'Buscar',

    // Form (crear/editar)
    formAddTitle: 'Añadir nuevo café',
    formEditTitle: 'Editar café #{id}',
    fieldCountry: 'País',
    fieldRegion: 'Región (ej: guji-hambela)',
    fieldVariety: 'Variedad',
    fieldProcessing: 'Procesado (ej: Washed / Wet)',
    fieldScore: 'Puntuación (0-100)',
    btnCreate: 'Crear café',
    btnSave: 'Guardar cambios',
    btnCancel: 'Cancelar',

    // Tabla
    thCountry: 'País',
    thRegion: 'Región',
    thVariety: 'Variedad',
    thProcessing: 'Procesado',
    thScore: 'Puntuación',
    thActions: 'Acciones',
    btnEdit: 'Editar',
    btnDelete: 'Borrar',
    emptyTable: 'No hay cafés para mostrar',

    // Paginación
    prevPage: 'Página anterior',
    nextPage: 'Página siguiente',
    pageStatus: 'Mostrando página {current} de {total} ({totalElements} cafés en total)',

    // Modal borrar
    deleteTitle: 'Borrar café',
    deleteMessage: 'Vas a borrar el café de',
    deleteWarning: 'Esta acción no se puede deshacer.',

    // Detalle expandible
    detailSpecies: 'Especie',
    detailHarvestYear: 'Año de cosecha',
    detailAltitude: 'Altitud (m)',
    detailAroma: 'Aroma',
    detailFlavor: 'Sabor',
    detailAftertaste: 'Postgusto',
    detailAcidity: 'Acidez',
    detailBody: 'Cuerpo',
    detailBalance: 'Balance',

    // Métodos de procesado
    proc_washed: 'Lavado / Húmedo',
    proc_natural: 'Natural / Seco',
    proc_pulped: 'Natural Despulpado',
    proc_honey: 'Miel',

    // Toasts
    countryRequired: 'El país es obligatorio',
    coffeeCreated: 'Café creado correctamente',
    coffeeUpdated: 'Café actualizado correctamente',
    coffeeDeleted: 'Café borrado correctamente',
    errorCreate: 'Error al crear',
    errorUpdate: 'Error al actualizar',
    errorDelete: 'Error al borrar'
  },

  en: {
    // Header
    login: 'Sign in',
    loginTooltip: 'Sign in with GitHub',
    logoutTooltip: 'Sign out',
    languageTooltip: 'Change language',
    roleAdmin: 'ADMIN',
    roleUser: 'USER',

    // Search
    searchPlaceholder: 'Search by country (e.g.: eth)',
    searchTooltip: 'Search',

    // Form (create/edit)
    formAddTitle: 'Add new coffee',
    formEditTitle: 'Edit coffee #{id}',
    fieldCountry: 'Country',
    fieldRegion: 'Region (e.g.: guji-hambela)',
    fieldVariety: 'Variety',
    fieldProcessing: 'Processing (e.g.: Washed / Wet)',
    fieldScore: 'Score (0-100)',
    btnCreate: 'Create coffee',
    btnSave: 'Save changes',
    btnCancel: 'Cancel',

    // Table
    thCountry: 'Country',
    thRegion: 'Region',
    thVariety: 'Variety',
    thProcessing: 'Processing',
    thScore: 'Score',
    thActions: 'Actions',
    btnEdit: 'Edit',
    btnDelete: 'Delete',
    emptyTable: 'No coffees to display',

    // Pagination
    prevPage: 'Previous page',
    nextPage: 'Next page',
    pageStatus: 'Showing page {current} of {total} ({totalElements} coffees total)',

    // Delete modal
    deleteTitle: 'Delete coffee',
    deleteMessage: 'You are about to delete the coffee from',
    deleteWarning: 'This action cannot be undone.',

    // Expandable detail
    detailSpecies: 'Species',
    detailHarvestYear: 'Harvest year',
    detailAltitude: 'Altitude (m)',
    detailAroma: 'Aroma',
    detailFlavor: 'Flavor',
    detailAftertaste: 'Aftertaste',
    detailAcidity: 'Acidity',
    detailBody: 'Body',
    detailBalance: 'Balance',

    // Processing methods
    proc_washed: 'Washed / Wet',
    proc_natural: 'Natural / Dry',
    proc_pulped: 'Pulped Natural',
    proc_honey: 'Honey',

    // Toasts
    countryRequired: 'Country is required',
    coffeeCreated: 'Coffee created successfully',
    coffeeUpdated: 'Coffee updated successfully',
    coffeeDeleted: 'Coffee deleted successfully',
    errorCreate: 'Error creating',
    errorUpdate: 'Error updating',
    errorDelete: 'Error deleting'
  }
} as const;

export type TranslationKey = keyof typeof messages.es;

/**
 * Devuelve la cadena traducida segun el idioma.
 * Sustituye placeholders {clave} por los valores pasados en params.
 */
export function translate(lang: Lang, key: TranslationKey, params?: Record<string, string | number>): string {
  let text: string = messages[lang][key] || key;
  if (params) {
    for (const [k, v] of Object.entries(params)) {
      text = text.replace(`{${k}}`, String(v));
    }
  }
  return text;
}
