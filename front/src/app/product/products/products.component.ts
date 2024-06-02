import { Component, OnInit } from '@angular/core';
import { Product } from "../product.model";
import mockedProducts from '../../../assets/products.json'
import { SelectItem } from "primeng/api";
import { SnackbarService } from "../../shared/utils/snackbar/snackbar.service";

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {

  products: Product[]
  sortOptions: SelectItem[];

  constructor(private snackbarService: SnackbarService) {
    this.sortOptions = [
      { label: 'Name ▲', value: 'asc-name' },
      { label: 'Name ▼', value: 'desc-name' },
      { label: 'Price ▲', value: 'asc-price' },
      { label: 'Price ▼', value: 'desc-price' },
      { label: 'Rating ▲', value: 'asc-rating' },
      { label: 'Rating ▼', value: 'desc-rating' },
      { label: 'Category', value: 'asc-category' },
      { label: 'Availability', value: 'desc-quantity' },
    ]
  }

  ngOnInit(): void {
    this.getData()
  }

  getData(): void {
    //TODO switch to the GET method when the backend is done

    // fill the products array with elements from the json file
    this.products = mockedProducts.data
  }

  addToCart(product: Product): void {
    this.snackbarService.displaySuccess(`Product [${product.name}] added to your cart!`)
    setTimeout(() => this.snackbarService.displayInfo('Well... In theory. There is no cart yet ☹️'),2000)
  }

  protected readonly Math = Math
}
