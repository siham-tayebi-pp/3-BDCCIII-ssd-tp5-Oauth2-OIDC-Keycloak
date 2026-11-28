
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.html',
  styleUrl: './orders.css',
})
export class Orders implements OnInit {

  public orders: any[] = [];

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {
    this.http.get<any[]>('http://localhost:8088/api/orders').subscribe({
      next: orders => {
        this.orders = orders;
      },
      error: err => {
        console.log(err);
      }
    });
  }

  getOrderDetails(o: any) {
    this.router.navigateByUrl('/order-details/' + o.id);
  }
}
