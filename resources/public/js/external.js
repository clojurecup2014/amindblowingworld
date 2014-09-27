
function findPosition(oElement) {
  if(typeof( oElement.offsetParent ) != "undefined") {
    for(var posX = 0, posY = 0; oElement; oElement = oElement.offsetParent) {
      posX += oElement.offsetLeft;
      posY += oElement.offsetTop;
    }
    return [ posX, posY ];
  }
  else {
    return [ oElement.x, oElement.y ];
  }
}

function external_getCoordinates(image, e, coordinatesCallback) {
  var PosX = 0;
  var PosY = 0;
  var ImgPos;
  ImgPos = findPosition(image);
  if (!e) var e = window.event;
  if (e.pageX || e.pageY) {
    PosX = e.pageX;
    PosY = e.pageY;
  }
  else if (e.clientX || e.clientY) {
    PosX = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
    PosY = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
  }
  PosX = PosX - ImgPos[0];
  PosY = PosY - ImgPos[1];
  coordinatesCallback(PosX, PosY);
}