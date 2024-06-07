import { Component, OnInit } from '@angular/core';
import { Product } from "../product.model";
import mockedProducts from '../../../assets/products.json'
import { SnackbarService } from "../../shared/utils/snackbar/snackbar.service";
import { CrudItemOptions } from "../../shared/utils/crud-item-options/crud-item-options.model";
import { ControlType } from "../../shared/utils/crud-item-options/control-type.model";
import { Validators } from "@angular/forms";

@Component({
  selector: 'app-products-admin',
  templateUrl: './products-admin.component.html',
  styleUrls: ['./products-admin.component.scss']
})
export class ProductsAdminComponent implements OnInit {

  products: Product[]
  config: CrudItemOptions[]

  constructor(private snackbarService: SnackbarService) {
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
    //TODO switch to the GET method when the backend is done

    // fill the products array with elements from the json file
    this.products = mockedProducts.data
  }

  deleteProduct(id: number): void {
    //TODO switch to the DELETE method when the backend is done

  }

  deleteProducts(ids: number[]): void {
    //TODO switch to the DELETE method when the backend is done

    this.localDeleteProducts(ids)
  }

  localDeleteProducts(ids: number[]): void {
    if (ids.length > 0) {

      let deleted: Product[] = []
      let failed: number = 0

      let index: number
      for (const id of ids) {
        index = this.products.findIndex(item => item.id === id)
        if (index > -1) {
          deleted.push(...this.products.splice(index, 1))
        } else {
          failed++
        }
      }

      if (deleted.length > 0 || failed > 0) {
        if (deleted.length > 0 && failed == 0) {
          this.snackbarService.displaySuccess(`${ deleted.length > 1 ? 'Products' : 'Product' } [${ deleted.map(product => product.code).join(', ') }] ${ deleted.length > 1 ? 'have' : 'has' } been deleted.`)
        } else if (deleted.length > 0 && failed > 0) {
          this.snackbarService.displayInfo(`${ deleted.length > 1 ? 'Products' : 'Product' } [${ deleted.map(product => product.code).join(', ') }] ${ deleted.length > 1 ? 'have' : 'has' } been deleted, 
          but it failed for the other ${ failed > 1 ? 'products' : 'product' }.`)
        } else {
          this.snackbarService.displayError(`${ failed > 1 ? 'Products' : 'Product' } could not be deleted.`)
        }
      }

    }
  }

  protected readonly Product = Product;
}