    String.prototype.toCapitalize = function () {

        var str = this.toLowerCase().replace(/\b[a-z]/g, function (letter) {
            return letter.toUpperCase();
        });

        return str;

    }
    
    String.prototype.endChar = function(a){
    	return this.slice(-1) == a;
    }
    
    Number.prototype.between = function (a, b) {

        var min = Math.min(a, b),
            max = Math.max(a, b);

        return this >= min && this <= max;
    };
  var style = document.createElement("style");

for(var i=1;i<600;i++){
    
    style.innerHTML += ".top-"+i+"{top:"+i+"px;}";
    style.innerHTML += ".top-minus-"+i+"{top:-"+i+"px;}";
    style.innerHTML += ".size-"+i+"{font-size:"+i+"px;}";
  
}

document.body.appendChild(style);