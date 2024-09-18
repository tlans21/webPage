// document.addEventListener('DOMContentLoaded', function() {
//     let isUsernameAvailable = false;

//     const emailSenderBtn = document.querySelector('.check-email-btn');

//     if (emailSenderBtn) {
//         emailSenderBtn.addEventListener("click", sendEmail);
//     } else {
//         alert(".check-email-btn DOM 생성되기 전에 스크립트 발생");
//     }

//     async function sendEmail() {
//         const email = document.getElementById("email").value;

//         if (!email) {
//             alert('이메일 주소를 입력해주세요.');
//             return;
//         }

//         try {
//             const response = await fetch('/send-mail', {
//                 method: 'POST',
//                 headers: {
//                     'Content-Type': 'application/json'
//                 },
//                 body: JSON.stringify({ email: email })
//             });

//             if (!response.ok) {
//                 throw new Error(`HTTP error! status: ${response.status}`);
//             }

//             const data = await response.json();
            
//             if (data.success) {
//                 alert('이메일 전송이 성공했습니다.');
//             } else {
//                 alert('이메일 전송이 실패했습니다. ' + (data.error || ''));
//             }
//         } catch (error) {
//             console.error('Error:', error);
//             alert('이메일 전송 중 오류가 발생했습니다: ' + error.message);
//         }
//     }
// });



// // 이메일 확인 코드를 위한 함수 추가
// async function verifyEmailCode(userNumber) {
//     try {
//         const response = await fetch(`/mailCheck?userNumber=${userNumber}`, {
//             method: 'GET'
//         });

//         const isMatch = await response.json();
        
//         if (isMatch) {
//             alert('이메일 인증이 성공했습니다.');
//         } else {
//             alert('이메일 인증이 실패했습니다. 코드를 다시 확인해주세요.');
//         }
//     } catch (error) {
//         console.error('Error:', error);
//         alert('이메일 인증 중 오류가 발생했습니다.');
//     }
// }