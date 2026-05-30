document.querySelectorAll('.chat-list-item').forEach(item => {
      item.addEventListener('click', () => {
          const userId = item.getAttribute('data-user-id');
          window.location.href = `/messages/chat/${userId}`;
      });
  });