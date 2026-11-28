import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Products } from './ui/products/products';
import { Customers } from './ui/customers/customers';

import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { Orders } from './ui/orders/orders';

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8180',
        realm: 'enset-bdcc-realme',
        clientId: 'ecom-client-ang',
      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false,
      },
    });
}

@NgModule({
  declarations: [App, Customers, Orders],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, Products, KeycloakAngularModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
  ],
  bootstrap: [App],
})
export class AppModule {}
