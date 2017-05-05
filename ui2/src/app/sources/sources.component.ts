import {Component, OnInit} from '@angular/core';
import {HttpService} from '../http.service';
import * as _ from "lodash";
import * as moment from 'moment';
import {NotificationsService} from "../notifications.service";
import {DataSourceFormComponent} from "../data-source-form/data-source-form.component";

@Component({
  selector: 'app-sources',
  templateUrl: './sources.component.html',
  styleUrls: ['./sources.component.css']
})
export class SourcesComponent implements OnInit {

  public model = {
    sources: [],
    dataProfiles: [],
    filter: {},
    groupNames: [],
    total: Number
  };

  constructor(private httpService: HttpService, private notificationService: NotificationsService) {
  }

  ngOnInit() {
    this.httpService.get("/dataProfiles").subscribe(response => {
      this.model.dataProfiles = response.json().map((it) => it.name);
      this.refresh();
    });
  }


  public refresh() {
    this.httpService.get("/dataSources").subscribe(response => {
      let data = response.json();
      this.model.groupNames = data.map(function (it) {
        return it.groupName;
      });
      this.model.groupNames = _.uniq(this.model.groupNames);

      this.model.total = data.length;
      let f: any = this.model.filter;
      let sources = _.filter(data, function (it: any) {
        return (
          (!f.groupName || f.groupName === it.groupName)
          && (!f.name || it.name.indexOf(f.name) >= 0)
          && (!f.profile || it.dataProfileName === f.profile)
          && (!f.url || it.url.indexOf(f.url) >= 0)
        );
      });
      this.model.sources = _.orderBy(sources, ['groupName', 'name']);
    });
  };

  public addSource = function () {
    this.editSource({
      enabled: true
    });
  };

  public editSource(source) {
    // this.modalService.open(DataSourceFormComponent);
    // .result.then((result) => {
    //   this.closeResult = `Closed with: ${result}`;
    // }, (reason) => {
    //   this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    // });

    // $uibModal.open({
    //   templateUrl: 'components/settings/dataSourceForm.html',
    //   controller: 'DataSourceFormController',
    //   resolve: {
    //     model: function () {
    //       return source;
    //     },
    //     groupNames: function () {
    //       return this.model.groupNames;
    //     },
    //     dataProfiles: function () {
    //       return this.model.dataProfiles;
    //     }
    //   }
    // }).result.then(function () {
    //   this.refresh();
    // });
  };

  public collect() {
    this.httpService.post("/collect", {}).subscribe(
      (r) => setTimeout(() => this.refresh(), 1000),
      (e) => this.notificationService.error(e.json())
    );
  };

  public getIcon(source) {
    if (source.status) {
      return source.status.ok ? 'fa-check-circle black' : 'fa-exclamation-triangle red';
    }
  };

  public getTitle(source) {
    if (source.status) {
      if (source.status.ok) {
        let m = moment(source.status.updated * 1000);
        return m.format('ll') + ' ' + m.format('HH:mm');
      } else {
        return source.status.errorMessage;
      }
    }
  };

  public onFilterSelect = function () {
    this.refresh();
  };

  public onKeyPress($event) {
    if ($event.keyCode === 13) {
      this.refresh();
    }
  };

}
