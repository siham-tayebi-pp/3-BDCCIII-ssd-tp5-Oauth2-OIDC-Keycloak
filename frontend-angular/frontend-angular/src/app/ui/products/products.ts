
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products.html',
  styleUrls: ['./products.css'],
})
export class Products implements OnInit {

  public products: any[] = [];

  constructor(
    private http: HttpClient,
    private keycloak: KeycloakService
  ) {}

  async ngOnInit(): Promise<void> {

    try {
      // Récupérer le token JWT depuis Keycloak
      const token = await this.keycloak.getToken();

      console.log('Token récupéré:', !!token);
      console.log('Rôles utilisateur:', this.keycloak.getUserRoles(true));

      // Ajouter le token dans le header Authorization
      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`
      });

      // Appeler le backend
      this.http.get<any[]>(
        'http://localhost:8087/api/products',
        { headers }
      ).subscribe({
        next: (data) => {
          this.products = data;
          console.log('Produits récupérés:', data);
        },
        error: (err) => {
          console.error('Erreur lors de la récupération des produits:', err);
        }
      });

    } catch (error) {
      console.error('Erreur Keycloak:', error);
    }
  }
}
