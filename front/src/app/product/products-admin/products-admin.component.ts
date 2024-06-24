import { Component, OnInit } from '@angular/core';
import { Product } from "../product.model";
import { SnackbarService } from "../../shared/utils/snackbar/snackbar.service";
import { CrudItemOptions } from "../../shared/utils/crud-item-options/crud-item-options.model";
import { ControlType } from "../../shared/utils/crud-item-options/control-type.model";
import { Validators } from "@angular/forms";
import { ProductService } from "../product.service";
import { DEFAULT_SEARCH_PARAMS, SearchParams } from "../../shared/ui/list/search.model";
import { LazyLoadEvent } from "primeng/api";
import { SearchService } from "../../shared/ui/list/search.service";
import { catchError, forkJoin, map, Observable, of } from "rxjs";

const DEFAULT_PRODUCT_SEARCH_PARAMS: SearchParams = { ...DEFAULT_SEARCH_PARAMS, sortField: 'id' }

@Component({
  selector: 'app-products-admin',
  templateUrl: './products-admin.component.html',
  styleUrls: ['./products-admin.component.scss']
})
export class ProductsAdminComponent implements OnInit {

  products: Product[] = []
  totalProducts: number = 0
  config: CrudItemOptions[]
  currentSearchParams: SearchParams = DEFAULT_PRODUCT_SEARCH_PARAMS

  constructor(private snackbarService: SnackbarService, private productService: ProductService, private searchService: SearchService) {
    this.config = [
      {
        key: 'category',
        label: 'category',
        controlType: ControlType.SELECT,
        options: [
          { value: 'placeholder', label: '- select one -', disabled: true },
          { value: 'Accessories', label: 'Accessories' },
          { value: 'Clothing', label: 'Clothing' },
          { value: 'Fitness', label: 'Fitness' },
          { value: 'Electronics', label: 'Electronics' },
        ],
        columnOptions: { hidden: true },
        controlOptions: { validators: [Validators.required] },
      },
      {
        key: 'code',
        label: 'code',
        controlType: ControlType.INPUT,
        type: 'text',
        columnOptions: { sortable: true, default: true },
        controlOptions: { validators: [Validators.required], hideOnUpdate: true },
      },
      {
        key: 'name',
        label: 'name',
        controlType: ControlType.INPUT,
        type: 'text',
        columnOptions: { sortable: true, default: true },
        controlOptions: { validators: [Validators.required] },
      },
      {
        key: 'price',
        label: 'price',
        controlType: ControlType.INPUT,
        type: 'number',
        columnOptions: { hidden: true },
        value: null,
      },
      {
        key: 'quantity',
        label: 'quantity',
        controlType: ControlType.INPUT,
        type: 'number',
        columnOptions: { hidden: true },
        controlOptions: { validators: [Validators.required] },
        value: null,
      },
      {
        key: 'description',
        label: 'description',
        controlType: ControlType.INPUT,
        type: 'text',
        columnOptions: { hidden: true }
      },
    ]
  }

  ngOnInit(): void {
    this.getData()
  }

  getData(): void {
    this.productService.getAll(DEFAULT_PRODUCT_SEARCH_PARAMS)
      .subscribe(response => {
        this.products = response._embedded.productDTOList
        this.totalProducts = response.page.totalElements
      })
  }

  updateProductList(event: LazyLoadEvent): void {
    let searchParams = this.searchService.mapToSearchParams(event, 'id')
    this.currentSearchParams = searchParams
    this.updateProductListWithSearchParams(searchParams)
  }

  updateProductListWithSearchParams(event: SearchParams): void {
    this.productService.getAll(event)
      .subscribe(response => {
        this.products = response._embedded ? response._embedded.productDTOList : []
        this.totalProducts = response.page.totalElements
      })
  }

  deleteProduct(id: number): Observable<boolean> {
    return this.productService.delete(id)
      .pipe(
        map(() => true),
        catchError(() => of(false))
      )
  }

  deleteProducts(ids: number[]): void {
    const requests: Observable<[number, boolean]>[] = ids.map(id =>
      this.deleteProduct(id).pipe(
        map(result => [id, result] as [number, boolean])
      )
    );

    forkJoin(requests).subscribe(
      ((results: [number, boolean][]) => {
        const idsDeleted: number[] = []
        const idsNotDeleted: number[] = []

        results.forEach(([id, deleted]) => {
          if (deleted) {
            idsDeleted.push(id)
          } else {
            idsNotDeleted.push(id)
          }
        })

        this.displayDeleteResults(idsDeleted, idsNotDeleted)
        this.updateProductListWithSearchParams(this.currentSearchParams)
      })
    )
  }

  displayDeleteResults(idsDeleted: number[], idsNotDeleted: number[]): void {
    if (idsNotDeleted.length === 0) {
      this.snackbarService.displaySuccess(`Product(s) [${ idsDeleted.join(', ') }] deleted successfully`)
    } else {
      if (idsDeleted.length !== 0) {
        this.snackbarService.displayInfo(`Product(s) [${ idsDeleted.join(', ') }] deleted successfully`)
      }
      this.snackbarService.displayError(`Product(s) [${ idsNotDeleted.join(', ') }] could not be deleted`)
    }
  }

  protected readonly Product = Product;
}
