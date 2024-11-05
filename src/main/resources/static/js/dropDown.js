function toggleDropdown() {
    document.getElementById("sortDropdown").classList.toggle("show");
}


 var currentSort = /*[[${sort}]]*/ 'latest';
        
 function changeSort(sortType, href) {
     let sortText;
     switch(sortType) {
         case 'latest':
             sortText = '최신순';
             break;
         case 'popular':
             sortText = '조회순';
             break;
         case 'comments':
             sortText = '댓글순';
             break;
     }
     document.getElementById("currentSort").innerText = sortText;
     window.location.href = href;
     return false;
 }



// 드롭다운 외부 클릭 시 닫기
window.onclick = function(event) {
    if (!event.target.matches('.dropdown-toggle')) {
        var dropdowns = document.getElementsByClassName("dropdown-menu");
        for (var i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
}