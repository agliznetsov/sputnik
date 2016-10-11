'use strict';

angular.module('sputnik').factory('httpUtils', function ($http) {

    var timeout = 5000;

    return {

        get: function (link) {
            return $http({
                method: 'GET',
                url: link,
                headers: {
                    'Accept': 'application/json'
                },
                cache: false,
                timeout: timeout
            });
        },

        post: function (link, body) {
            return $http({
                method: 'POST',
                url: link,
                data: body,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                cache: false,
                timeout: timeout
            });
        },

        delete: function (link) {
            return $http({
                method: 'DELETE',
                url: link,
                headers: {
                    'Accept': 'application/json'
                },
                cache: false,
                timeout: timeout
            });
        },

        put: function (link, body) {
            return $http({
                method: 'PUT',
                url: link,
                data: body,
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                cache: false,
                timeout: timeout
            });
        }

    };
});


