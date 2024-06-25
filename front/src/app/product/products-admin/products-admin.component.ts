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
import { catchError, forkJoin, map, Observable, of, tap, throwError } from "rxjs";
import { PatchProduct } from "../patch-product.model";

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
          { value: 'ACCESSORIES', label: 'Accessories' },
          { value: 'CLOTHING', label: 'Clothing' },
          { value: 'FITNESS', label: 'Fitness' },
          { value: 'ELECTRONICS', label: 'Electronics' },
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

  createProduct(product: Product) {
    const patchProduct = this.productService.productToPatchProduct(product);

    this.productService.create(patchProduct).pipe(

      tap(() => {
        this.snackbarService.displaySuccess('Product created successfully')

        let searchParams: SearchParams = { ...DEFAULT_PRODUCT_SEARCH_PARAMS, sortField: 'id', sortOrder: 'desc' }
        this.currentSearchParams = searchParams
        this.updateProductListWithSearchParams(searchParams)
      }),

      catchError(error => {
        this.snackbarService.displayError('Product creation failed')
        return throwError(error)
      })
    ).subscribe()
  }

  updateProduct(product: Product) {

    const originalProduct = this.products.find(value => value.code === product.code);

    if (originalProduct === undefined) {
      this.snackbarService.displayError(`Couldn't find product with code [${ product.code }]`)
      return
    }

    const patchedValues = this.getPatchedValues(originalProduct, product)

    if (patchedValues === undefined) {
      this.snackbarService.displayInfo('New product is identical to original, update cancelled')
    }

    this.productService.update(originalProduct.id, patchedValues)
      .pipe(

       tap(updatedProduct => {
         this.snackbarService.displaySuccess(`Product was updated successfully`)

         // Update the product in products list. If not found, update the list.
         let index = this.products.findIndex(value => value.id === updatedProduct.id)
         if (index !== -1) {
           this.products[index] = updatedProduct
         } else {
           this.updateProductListWithSearchParams(this.currentSearchParams)
         }
       }),

        catchError(error => {
          this.snackbarService.displayError(`The product couldn't be updated`)
          return throwError(error)
        })

      ).subscribe()
  }

  getPatchedValues(originalProduct: Product, editedProduct: Product) {
    let patchedValues = new PatchProduct();
    let modified = false;

    if (editedProduct.code !== originalProduct.code) {
      patchedValues.code = editedProduct.code
      modified = true
    }

    if (editedProduct.name !== originalProduct.name) {
      patchedValues.name = editedProduct.name
      modified = true
    }

    if (editedProduct.description !== originalProduct.description) {
      patchedValues.description = editedProduct.description
      modified = true
    }

    if (editedProduct.price !== originalProduct.price) {
      patchedValues.price = editedProduct.price
      modified = true
    }

    if (editedProduct.quantity !== originalProduct.quantity) {
      patchedValues.quantity = editedProduct.quantity
      modified = true
    }

    if (editedProduct.category !== originalProduct.category) {
      patchedValues.category = editedProduct.category
      modified = true
    }

    if (editedProduct.image !== originalProduct.image) {
      patchedValues.image = editedProduct.image
      modified = true
    }

    if (editedProduct.rating !== originalProduct.rating) {
      patchedValues.rating = editedProduct.rating
      modified = true
    }

    if (modified) {
      return patchedValues
    } else {
      return undefined
    }
  }

  protected readonly Product = Product;
}
