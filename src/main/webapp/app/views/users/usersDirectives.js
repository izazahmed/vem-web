/*app.directive("maxlength20", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength20"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength20 = function (modelValue) {
                    if (modelValue) {

                        return modelValue.length > 20 ? false : true;
                    }

                    return true;
                };

                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("maxlength32", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=maxlength32"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.maxlength32 = function (modelValue) {
                    if (modelValue) {

                        return modelValue.length > 32 ? false : true;
                    }

                    return true;
                };

                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })
    .directive("selectValidation", function () {
        return {
            require: "ngModel",
            scope: {
                otherModelValue: "=selectValidation"
            },
            link: function (scope, element, attributes, ngModel) {

                ngModel.$validators.selectValidation = function (modelValue) {

                    if (modelValue) {

                        return true;
                    } else {
                        return false;
                    }
                };

                scope.$watch("otherModelValue", function () {
                    ngModel.$validate();
                });
            }
        };
    })*/