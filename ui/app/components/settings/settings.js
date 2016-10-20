'use strict';

angular.module('sputnik').controller('SettingsController', function ($scope, $routeParams, $q, $uibModal, httpUtils, notificationService) {

    $scope.model = {
        sources: [],
        dataProfiles: []
    };

    $scope.init = function () {
        httpUtils.get("/dataProfiles").then(function (response) {
            $scope.model.dataProfiles = response.data.map(function (it) {
                return it.name;
            });
            $scope.refresh();
        });
    };

    $scope.refresh = function () {
        httpUtils.get("/dataSources").then(function (response) {
            $scope.model.sources = _.orderBy(response.data, ['groupName', 'name']);
        })
    };

    $scope.addSource = function () {
        $scope.editSource({
            enabled: true
        });
    };

    $scope.editSource = function (source) {
        $uibModal.open({
            templateUrl: 'components/settings/dataSourceForm.html',
            controller: 'DataSourceFormController',
            resolve: {
                model: function () {
                    return source;
                },
                dataProfiles: function () {
                    return $scope.model.dataProfiles;
                }
            }
        }).result.then(function () {
            $scope.refresh();
        });
    };

    $scope.collect = function () {
        httpUtils.post("/collect").catch(function (err) {
            notificationService.error(err);
        });
    };

    $scope.getIcon = function (source) {
        if (source.status) {
            return source.status.ok ? 'fa-check-circle black' : 'fa-exclamation-triangle red';
        }
    };

    $scope.getTitle = function (source) {
        if (source.status) {
            if (source.status.ok) {
                var moment = window.moment(source.status.time * 1000);
                return moment.format('ll') + ' ' + moment.format('HH:mm');
            } else {
                return source.status.errorMessage;
            }
        }
    };

    $scope.init();

});
