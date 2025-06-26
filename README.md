🏋️‍♂️ FitUnifor
Aplicativo Android para gerenciamento de treinos e aulas coletivas da academia da Universidade de Fortaleza (Unifor), desenvolvido em Kotlin com integração ao Firebase.

📱 Sobre o Projeto

O FitUnifor é um app mobile voltado para alunos e profissionais da academia da Unifor. Ele permite o acompanhamento das fichas de treino dos alunos, organização de aulas coletivas e gerenciamento de dados por parte dos administradores (personais).

Com uma interface intuitiva, o app facilita o dia a dia dos usuários com acesso rápido às informações e recursos organizados de forma clara e eficiente.

👥 Perfis de Usuário

🧍 Usuário Comum (Aluno)
- Login com Firebase Authentication.
- Acesso à ficha de treino personalizada.
- Visualização do calendário com treinos e aulas coletivas.
- Filtro com ToggleButtons para alternar entre treinos e aulas no calendário.
- Recuperação de senha*por e-mail (enviada automaticamente pelo Firebase).
- Acesso à vídeos de cada exercício
- Pode Iniciar e Finalizar um Treino
- Acesso a quantidade de Treinos completos e diferentes níveis relacionados à quantidade de Treinos finalizados
- Acesso ao histórico de Treinos
- Acesso ao um bate-papo com IA

🧑‍🏫 Administrador (Personal)
- Login com permissão administrativa.
- Acesso à lista de alunos
- Acesso ao CRUD completo de:
  - Fichas de treino para cada aluno.
  - Filtragem de exercícios por músculo
  - Aulas coletivas

🔐 Autenticação

A autenticação é gerenciada pelo Firebase Authentication, permitindo:
- Criação e login de usuários com e-mail e senha.
- Redefinição de senha via e-mail.
- Verificação de usuários autenticados no app.

☁️ Integrações com Firebase

- Firebase Authentication – Login e gerenciamento de conta.
- Cloud Firestore – Armazenamento das fichas de treino, aulas e usuários.

🛠️ Tecnologias Utilizadas

- **Kotlin**
- **Android Studio**
- **XML** para layouts
- **Firebase Authentication**
- **Cloud Firestore**
- **Firebase Storage**
- **Material Design Components**


