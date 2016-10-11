'use strict';

angular.module('sputnik').controller('MainController', function ($scope, $rootScope, $location) {

    $scope.menu = {
        left: [
            {
                name: 'Settings',
                url: 'settings',
                iconClass: 'fa-search'
            }
        ],
        right: []
    };

    $scope.getUrl = function (item) {
        return '#/' + item.url;
    };

    $scope.isActive = function (path) {
        return $location.path().indexOf(path) === 0;
    };

});
