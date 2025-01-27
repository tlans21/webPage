// button 동작을 a 태그를 지정했을 때 동작하도록 전이시키는 코드

document.addEventListener('click', function(e) {
    if (e.target && e.target.id === 'sidebarToggle') {
        const specificSidebar = document.getElementById('sidebar');
        if (specificSidebar) {
            const menuItems = specificSidebar.querySelectorAll('.menu-item');
            menuItems.forEach(item => {
                item.addEventListener('click', (e) => {
                    const button = item.querySelector('button');
                    if (button) {
                        button.click();
                    }
                    const link = item.querySelector('a');
                    if (link) {
                        window.location.href = link.href;
                    }
                });
            });
        }
    }
})

