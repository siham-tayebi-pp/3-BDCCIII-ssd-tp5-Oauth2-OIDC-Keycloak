
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';

export const authGuard: CanActivateFn = async (route, state) => {

  const keycloak = inject(KeycloakService);
  const router = inject(Router);

  // 1. Vérifier l'authentification
  const authenticated = await keycloak.isLoggedIn();

  // 2. Si l'utilisateur n'est pas connecté, rediriger vers Keycloak
  if (!authenticated) {
    await keycloak.login({
      redirectUri: window.location.origin + state.url
    });

    return false;
  }

  // 3. Récupérer les rôles exigés par la route
  const requiredRoles: string[] = route.data['roles'] ?? [];

  // 4. Si aucun rôle n'est exigé, autoriser l'accès
  if (requiredRoles.length === 0) {
    return true;
  }

  // 5. Récupérer les rôles de l'utilisateur depuis Keycloak
  const userRoles: string[] = keycloak.getUserRoles(true);

  // 6. Vérifier si l'utilisateur possède au moins un rôle requis
  const hasRequiredRole = requiredRoles.some(role =>
    userRoles.includes(role)
  );

  // 7. Refuser l'accès si l'utilisateur ne possède aucun rôle requis
  if (!hasRequiredRole) {
    await router.navigate(['/']);
    return false;
  }

  return true;
};
