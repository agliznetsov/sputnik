import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-data-source-form',
  templateUrl: './data-source-form.component.html',
  styleUrls: ['./data-source-form.component.css']
})
export class DataSourceFormComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

//   function init() {
//   $scope.model = angular.copy(model);
//   $scope.dataFormats = ['JSON', 'PROPERTIES'];
//   $scope.dataProfiles = dataProfiles;
//   $scope.groupNames = groupNames;
// }
//
//   function save() {
//   return httpUtils.post("/dataSources/", $scope.model);
// }
//
//   $scope.clickOk = function (form) {
//   if (!form || form.$valid) {
//     save().then(function () {
//       $uibModalInstance.close();
//     }, function (error) {
//       $scope.error(error);
//     });
//   }
// };
//
//   $scope.clickCancel = function () {
//   $uibModalInstance.dismiss('cancel');
// };
//
//   $scope.clickRemove = function () {
//   modal.confirm("Are you sure you want to remove this data source?").then(function () {
//     httpUtils.delete("/dataSources/" + $scope.model.id).then(function () {
//       $uibModalInstance.close();
//     }, function (err) {
//       $scope.error(err);
//     });
//   });
// };
//
//   $scope.error = function (response) {
//   if (response.data && response.data.message)
//     $scope.errorMessage = response.data.message;
//   else
//     $scope.errorMessage = 'Request failed!';
// };

}
