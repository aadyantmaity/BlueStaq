import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./poem-list/poem-list.component').then(m => m.PoemListComponent)
  },
  {
    path: 'poem/:id',
    loadComponent: () => import('./poem-detail/poem-detail.component').then(m => m.PoemDetailComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];

