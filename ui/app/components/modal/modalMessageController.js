'use strict';

angular.module('sputnik').controller('ModalMessageController', function ($scope, $uibModalInstance, options) {
    if (!options) {
        options = {};
    }

    $scope.options = options;
    $scope.message = options.message || 'Are you sure you want to perform this action?';
    $scope.title = options.title || 'Message';

    $scope.clickOk = function (form) {
        if (!form || form.$valid) {
            $uibModalInstance.close($scope.options.response);
        }
    };

    $scope.clickYes = function () {
        $uibModalInstance.close('yes');
    };

    $scope.clickNo = function () {
        $uibModalInstance.close('no');
    };

    $scope.clickCancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

});
