import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import { ModalModule } from 'ngx-bootstrap';

import {AppComponent} from './app.component';
import {AboutComponent} from './about/about.component';
import {AppRoutingModule} from './app-routing.module';
import {HomeComponent} from './home/home.component';
import {SourcesComponent} from './sources/sources.component';
import {ProfilesComponent} from './profiles/profiles.component';
import {SigninComponent} from './signin/signin.component';
import {DataSourceFormComponent } from './data-source-form/data-source-form.component';


@NgModule({
  imports: [
    AppRoutingModule,
    BrowserModule,
    FormsModule,
    HttpModule,
    ModalModule.forRoot()
  ],
  declarations: [
    AppComponent,
    AboutComponent,
    HomeComponent,
    SourcesComponent,
    ProfilesComponent,
    SigninComponent,
    DataSourceFormComponent
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [DataSourceFormComponent]
})
export class AppModule {
}
