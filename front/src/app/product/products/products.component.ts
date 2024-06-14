import { Component, OnInit } from '@angular/core';
import { Product } from "../product.model";
import { SelectItem } from "primeng/api";
import { SnackbarService } from "../../shared/utils/snackbar/snackbar.service";
import { LoadingService } from "../../shared/utils/loading/loading.service";
import { PaginationEvent } from "../../shared/ui/list/list.component";
import { ProductService } from "../product.service";
import { DEFAULT_SEARCH_PARAMS, SearchParams } from "../../shared/ui/list/search.model";

const DEFAULT_PRODUCT_SEARCH_PARAMS: SearchParams = { ...DEFAULT_SEARCH_PARAMS, sortField: 'id'}

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {

  products: Product[] = []
  totalProducts: number = 0
  sortOptions: SelectItem[];

  constructor(private snackbarService: SnackbarService, private loadingService: LoadingService, private productService: ProductService) {
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
    this.productService.getAll(DEFAULT_PRODUCT_SEARCH_PARAMS)
      .subscribe(response => {
        console.log('response : ', response)
        this.products = response._embedded.productDTOList
        this.totalProducts = response.page.totalElements
        this.loadingService.stop()
      })

  }

  updateProductList(event: PaginationEvent): void {
    console.log(event)

    this.loadingService.start("spin")
    this.productService.getAll({ ...DEFAULT_PRODUCT_SEARCH_PARAMS, ...event })
      .subscribe(response => {
        console.log('response : ', response)
        this.products = response._embedded.productDTOList
        this.totalProducts = response.page.totalElements
        this.loadingService.stop()
      })
  }

  addToCart(product: Product): void {
    this.snackbarService.displaySuccess(`Product [${product.name}] added to your cart!`)
    setTimeout(() => this.snackbarService.displayInfo('Well... In theory. There is no cart yet ☹️'),2000)
  }

  protected readonly Math = Math
}
