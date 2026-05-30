async function checkMutualStatus(userId, button) {
      try {
          const checkResponse = await fetch(`/api/likes/check/${userId}`, {
              method: 'GET',
              headers: { 'Content-Type': 'application/json' }
          });
          const checkData = await checkResponse.json();

          if (checkData.liked) {
              const mutualCheck = await fetch(`/api/likes/check-mutual/${userId}`);
              const mutualData = await mutualCheck.json();

              if (mutualData.mutual) {
                  button.textContent = '💬 Написать';
                  button.classList.remove('btn-success');
                  button.classList.add('btn-primary');
                  button.onclick = () => window.location.href = `/messages/chat/${userId}`;
              } else {
                  button.textContent = ' Лайк отправлен';
                  button.classList.remove('btn-success');
                  button.classList.add('btn-secondary');
                  button.disabled = true;
              }
          } else {
              button.textContent = '❤️ Лайк в ответ';
              button.classList.remove('btn-primary', 'btn-secondary');
              button.classList.add('btn-success');
              button.disabled = false;
              button.onclick = () => handleLike(userId, button);
          }
      } catch (error) {
          console.error('Ошибка проверки статуса:', error);
          button.textContent = '❤️ Лайк в ответ';
          button.classList.add('btn-success');
          button.onclick = () => handleLike(userId, button);
      }
  }

  async function handleLike(userId, button) {
      const username = button.getAttribute('data-username');

      try {
          const response = await fetch(`/api/likes/mutual/${userId}`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' }
          });
          const data = await response.json();

          if (data.mutual) {
              button.textContent = '💬 Написать';
              button.classList.remove('btn-success');
              button.classList.add('btn-primary');
              button.onclick = () => window.location.href = `/messages/chat/${userId}`;
              alert(`Взаимный лайк с ${username}! Теперь вы можете начать чат`);
          } else if (data.liked) {
              button.textContent = ' Лайк отправлен';
              button.classList.remove('btn-success');
              button.classList.add('btn-secondary');
              button.disabled = true;
          } else {
              alert(data.message);
          }
      } catch (error) {
          console.error('Ошибка:', error);
          alert('Не удалось поставить лайк');
      }
  }

  document.addEventListener('DOMContentLoaded', async () => {
      const buttons = document.querySelectorAll('.like-action-btn');

      for (const btn of buttons) {
          const userId = btn.getAttribute('data-user-id');
          await checkMutualStatus(userId, btn);
      }
  });