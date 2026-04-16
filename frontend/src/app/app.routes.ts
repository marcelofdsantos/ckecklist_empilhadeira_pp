import { Routes } from '@angular/router';
import { LoginComponent }  from './pages/login/login';
import { Checklist }       from './pages/checklist/checklist';
import { AdminComponent }  from './pages/admin/admin';
import { authGuard }       from './guards/auth.guard';
import { adminGuard }      from './guards/admin.guard';

export const routes: Routes = [
  { path: 'login',    component: LoginComponent },
  { path: 'checklist', component: Checklist,       canActivate: [authGuard] },
  { path: 'admin',    component: AdminComponent,   canActivate: [adminGuard] },
  { path: '',         redirectTo: 'login',          pathMatch: 'full' }
];
