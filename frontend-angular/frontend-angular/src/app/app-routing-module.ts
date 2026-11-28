import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Products} from './ui/products/products';
import {Customers} from './ui/customers/customers';
import {KeycloakAuthGuard} from 'keycloak-angular';
import {authGuard} from './guards/auth-guard';
import {Orders} from './ui/orders/orders';
import {OrderDetails} from './ui/order-details/order-details';
// import { authGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: 'products', component: Products,canActivate:[authGuard] ,data: { roles: ['ADMIN'] } },
  { path: 'customers', component: Customers,canActivate:[authGuard] ,data: { roles: ['USER'] } },
  { path: 'orders', component: Orders,canActivate:[authGuard] ,data: { roles: ['USER'] } },
  {path : "order-details/:id", component : OrderDetails, canActivate : [authGuard], data : {roles : ['USER']}},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
