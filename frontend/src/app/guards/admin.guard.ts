import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { podeAcessarPainel } from '../models/api.models';

export const adminGuard = () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const user = auth.getCurrentUser();
  if (user && podeAcessarPainel(user.perfil)) {
    return true;
  }

  // Autenticado mas sem permissão de painel → volta para checklist
  router.navigate(['/checklist']);
  return false;
};
