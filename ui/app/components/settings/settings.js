'use strict';

angular.module('sputnik').controller('SettingsController', function ($scope, $routeParams, $q, $uibModal, httpUtils) {

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

    $scope.init();

});
