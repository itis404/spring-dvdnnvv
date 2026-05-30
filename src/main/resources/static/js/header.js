<script>
  async function updateNotificationCount() {
      try {
          const response = await fetch('/notifications/count', {
              credentials: 'include'
          });
          const count = await response.json();
          const badge = document.getElementById('notificationBadge');
          if (badge) {
              if (count > 0) {
                  badge.textContent = count > 99 ? '99+' : count;
                  badge.style.display = 'inline-block';
              } else {
                  badge.style.display = 'none';
              }
          }
      } catch (error) {
          console.error('Ошибка загрузки уведомлений:', error);
      }
  }

  document.addEventListener('DOMContentLoaded', function() {
      updateNotificationCount();
      setInterval(updateNotificationCount, 30000);
  });
</script>