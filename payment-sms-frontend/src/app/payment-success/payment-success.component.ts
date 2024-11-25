import { Component } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [],
  templateUrl: './payment-success.component.html',
  styleUrl: './payment-success.component.css'
})
export class PaymentSuccessComponent {
  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    // Capture the payment reference and status from the URL query params
    this.route.queryParams.subscribe((params) => {
      const reference = params['reference'];
      const status = params['status']; // 'success' or 'failure'

      if (status === 'success') {
        // Handle successful payment logic here
        console.log('Payment Successful', reference);
        // Optionally redirect to a confirmation page or dashboard
        this.router.navigate(['/success']);
      } else {
        console.log('Payment Failed');
        // Optionally show an error message or retry payment
      }
    });
  }
}
