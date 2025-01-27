document.addEventListener('DOMContentLoaded', function() {
    const sidebarToggle = document.getElementById('sidebarToggle');
    const closeSidebar = document.getElementById('closeSidebar');
    const sidebar = document.getElementById('sidebar');
    
    function toggleSidebar() {
        sidebar.classList.toggle('active');
    }

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', toggleSidebar);
    }

    if (closeSidebar) {
        closeSidebar.addEventListener('click', toggleSidebar);
    }

    // 사이드바 외부 클릭시 닫기
    document.addEventListener('click', function(event) {
        const isClickInside = sidebar.contains(event.target) || 
                            sidebarToggle.contains(event.target);
        
        if (!isClickInside && sidebar.classList.contains('active')) {
            toggleSidebar();
        }
    });
});

