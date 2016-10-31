'use strict';

angular.module('sputnik').controller('SettingsController', function ($scope, $routeParams, $q, $uibModal, $timeout, httpUtils, notificationService) {

    $scope.model = {
        sources: [],
        dataProfiles: [],
        filter: {}
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
            $scope.model.groupNames = response.data.map(function(it) {return it.groupName});
            $scope.model.groupNames = _.uniq($scope.model.groupNames);

            $scope.model.total = response.data.length;
            var f = $scope.model.filter;
            var sources = _.filter(response.data, function (it) {
                return (
                    (!f.groupName || f.groupName === it.groupName)
                    && (!f.name || it.name.indexOf(f.name) >= 0)
                    && (!f.profile || it.dataProfileName === f.profile)
                    && (!f.url || it.url.indexOf(f.url) >= 0)
                );
            });
            $scope.model.sources = _.orderBy(sources, ['groupName', 'name']);
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
                groupNames: function() {
                    return $scope.model.groupNames;
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
        httpUtils.post("/collect").then(function () {
                $timeout($scope.refresh, 1000);
            },
            function (err) {
                notificationService.error(err);
            }
        );
    };

    $scope.getIcon = function (source) {
        if (source.status) {
            return source.status.ok ? 'fa-check-circle black' : 'fa-exclamation-triangle red';
        }
    };

    $scope.getTitle = function (source) {
        if (source.status) {
            if (source.status.ok) {
                var moment = window.moment(source.status.updated * 1000);
                return moment.format('ll') + ' ' + moment.format('HH:mm');
            } else {
                return source.status.errorMessage;
            }
        }
    };

    $scope.onFilterSelect = function () {
        $scope.refresh();
    };

    $scope.onKeyPress = function ($event) {
        if ($event.keyCode === 13) {
            $scope.refresh();
        }
    };

    $scope.init();

});
