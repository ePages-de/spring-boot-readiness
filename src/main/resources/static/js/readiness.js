'use strict';
function setTheme(theme) {
  document.documentElement.setAttribute('data-bs-theme', theme);
  var button = document.querySelector('#color-mode-selector');
  var currentQuery = new URLSearchParams(
    (window.location.search || '').substring(1),
  );

  if (theme === 'dark') {
    document.documentElement.setAttribute('data-bs-theme', 'light');
    button.textContent = 'Enable Dark Mode';
    currentQuery.set('theme', 'light');
  } else {
    document.documentElement.setAttribute('data-bs-theme', 'dark');
    button.textContent = 'Enable Light Mode';
    currentQuery.set('theme', 'dark');
  }

  window.history.replaceState(
    '',
    document.title,
    window.location.pathname + '?' + currentQuery.toString(),
  );
}

function determineCurrentTheme() {
  if (window.matchMedia('(prefers-color-scheme: dark)').matches) {
    setTheme('dark');
  } else if (window.matchMedia('(prefers-color-scheme: light)').matches) {
    setTheme('light');
  } else if (window.location.search.includes('theme=dark')) {
    setTheme('dark');
  } else if (window.location.search.includes('theme=light')) {
    setTheme('light');
  }
}

function main() {
  determineCurrentTheme();

  document
    .querySelector('#color-mode-selector')
    .addEventListener('click', function () {
      setTheme(document.documentElement.getAttribute('data-bs-theme'));
    });

  Array.from(document.querySelectorAll('[data-bs-toggle="popover"]')).forEach(
    function (popover) {
      new window.bootstrap.Popover(popover, {
        html     : true,
        trigger  : 'hover',
        placement: 'auto',
        content  : function () {
          return popover.querySelector('.popover-content').innerHTML;
        },
        title: function () {
          return popover.querySelector('.popover-heading').innerHTML;
        },
      });
    },
  );
}

main();
