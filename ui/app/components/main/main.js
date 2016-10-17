'use strict';

angular.module('sputnik').controller('MainController', function ($scope, $rootScope, $location, notificationService) {

    $scope.menu = {
        left: [
            {
                name: 'Settings',
                url: 'settings',
                icon: 'fa-cog'
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

    $scope.removeNotification = function (key) {
        notificationService.removeNotification(key);
    };

});
