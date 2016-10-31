'use strict';

angular.module('sputnik', [
    'ngRoute', 'ui.bootstrap'
]).config(function ($routeProvider, $httpProvider) {
    $routeProvider
        .when('/home', {
            templateUrl: 'components/home/home.html'
        })
        .when('/sources', {
            templateUrl: 'components/settings/settings.html'
        })
        .when('/profiles', {
            templateUrl: 'components/profiles/profiles.html'
        })
        .when('/signin', {
            templateUrl: 'components/signin/signin.html'
        })
        .otherwise({
            redirectTo: '/home'
        });

});
