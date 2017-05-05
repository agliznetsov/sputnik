import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {HomeComponent} from './home/home.component';
import {SourcesComponent} from './sources/sources.component';
import {ProfilesComponent} from 'app/profiles/profiles.component';
import {SigninComponent} from './signin/signin.component';

const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'home', component: HomeComponent},
  {path: 'sources', component: SourcesComponent},
  {path: 'profiles', component: ProfilesComponent},
  {path: 'signin', component: SigninComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
