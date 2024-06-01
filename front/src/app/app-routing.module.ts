import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsComponent } from "./product/products/products.component";
import { ProductsAdminComponent } from "./product/products-admin/products-admin.component";

const routes: Routes = [
  {
    path: '',
    children: [
      { path: 'products', component: ProductsComponent },
      {
        path: 'admin',
        children: [
          { path: 'products', component: ProductsAdminComponent },
          { path: '', redirectTo: 'products', pathMatch: 'full' },
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule],
})

export class AppRoutingModule {}
