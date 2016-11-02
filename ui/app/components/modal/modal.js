'use strict';

angular.module('sputnik').factory('modal', function ($uibModal) {

    var api = {
        message: function(options) {
            return $uibModal.open({
                templateUrl: 'components/modal/modalMessage.html',
                controller: 'ModalMessageController',
                resolve: {
                    options: function () {
                        return options;
                    }
                }
            }).result;
        },
        confirm: function(text, title) {
            return api.message({
                icon: "fa-warning",
                title: title || 'Are you sure?',
                message: text
            });
        }
    };

    return api;
});
