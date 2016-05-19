angular.module('vendorInfo', []).controller('vendorInfoController',

function($http, $scope) {

        $scope.headerText = 'Vendor Information';

        $scope.vendors = [];

        $scope.init = function () {
            $scope.load();
        };

         $scope.load = function() {
            var getResponse = $http.get('vendorInfo');
            getResponse.success(function(data, status, headers, config) {
                   $scope.vendors = data;
             });
            getResponse.error(function(data, status, headers, config) {
                   alert( "Something went wrong: " + JSON.stringify({data: data}));
             });
         };

        $scope.submit = function() {
            var vendor = {
                "name" : $scope.name,
                "address" : $scope.address,
                "phone" : $scope.phone
            };
            var response = $http.post('addVendor', JSON.stringify(vendor));
            response.success(function(data, status, headers, config) {
                $scope.onAddVendorSuccess();
            });
            response.error(function(data, status, headers, config) {
                alert( "Something went wrong: " + status);
            });
        };

        $scope.onAddVendorSuccess = function() {
           $scope.load();
        }
});

