// MapUtils.js
export function createMap(mapx, mapy) {
    try {
        console.log("Creating map with coordinates:", mapx, mapy);
        var tm128 = new naver.maps.Point(mapx, mapy);
        var latLng = convertToLatLng(mapx, mapy);
        var mapOptions = {
            center: new naver.maps.LatLng(latLng.y, latLng.x),
            zoom: 17,
        };

        var map = new naver.maps.Map('map', mapOptions);

        var marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(latLng.y, latLng.x),
            map: map
        });
        
        console.log("Map created successfully");
        return map;
    } catch (error) {
        console.error("Error creating map:", error);
        throw error;
    }
}

export function convertToLatLng(mapx, mapy) {
    // 10^7로 나누어 실제 위도와 경도 값으로 변환
    var lat = mapy / 10000000;
    var lng = mapx / 10000000;
    return { y: lat, x: lng };
}

export function loadModalStyle() {
    if (!document.getElementById('mapModal-content')) {
        var link = document.createElement('link');
        link.id = "mapModalStyle";
        link.rel = "stylesheet";
        link.type = "text/css";
        link.href = "/css/mapModalStyle.css";
        document.head.appendChild(link);
    }
}