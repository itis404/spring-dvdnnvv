/*<![CDATA[*/
  const currentUserId = /*[[${currentUser.id}]]*/ null;
  /*]]>*/

  async function checkLikeStatus(profileId, btn) {
      try {
          const response = await fetch(`/api/likes/check/${profileId}`);
          const data = await response.json();
          if (data.liked) {
              btn.classList.add('active');
              btn.textContent = '❤️';
              btn.setAttribute('data-liked', 'true');
          } else {
              btn.classList.remove('active');
              btn.textContent = '🤍';
              btn.setAttribute('data-liked', 'false');
          }
      } catch (error) {
          console.error('Ошибка проверки лайка:', error);
      }
  }

  async function handleLike(profileId, btn) {
      try {
          const response = await fetch(`/api/likes/${profileId}`, {
              method: 'POST',
              headers: { 'Content-Type': 'application/json' }
          });
          const data = await response.json();

          if (data.liked) {
              btn.classList.add('active');
              btn.textContent = '❤️';
              btn.setAttribute('data-liked', 'true');
          } else {
              btn.classList.remove('active');
              btn.textContent = '🤍';
              btn.setAttribute('data-liked', 'false');
          }
      } catch (error) {
          console.error('Ошибка при лайке:', error);
          alert('Не удалось поставить лайк. Попробуйте позже.');
      }
  }

  document.addEventListener('DOMContentLoaded', async () => {
      const likeBtns = document.querySelectorAll('.like-btn');

      for (const btn of likeBtns) {
          const profileId = btn.getAttribute('data-profile-id');
          await checkLikeStatus(profileId, btn);
      }

      for (const btn of likeBtns) {
          btn.addEventListener('click', async (e) => {
              e.preventDefault();
              const profileId = btn.getAttribute('data-profile-id');
              await handleLike(profileId, btn);
          });
      }
  });