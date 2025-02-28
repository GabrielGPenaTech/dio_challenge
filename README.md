## Projeto de Board para Gerenciamento de Tarefas

Desenvolva um código que cria um board customizável para acompanhamento de tarefas.

### Requisitos

1. O código deve iniciar disponibilizando um menu com as seguintes opções: Criar novo board, Selecionar board, Excluir board.
2. O código deve salvar o board com suas informações no banco de dados MySQL.

### Regras dos Boards

1. Um board deve ter um nome e ser composto por pelo menos 3 colunas: coluna inicial, coluna de cancelamento e coluna final.
2. As colunas têm seu respectivo nome, ordem de aparição no board e seu tipo (Inicial, Cancelamento, Final e Pendente).
3. Cada board só pode ter 1 coluna do tipo Inicial, Cancelamento e Final; colunas do tipo Pendente podem ser múltiplas.
4. As colunas podem conter 0 ou N cards. Cada card possui título, descrição, data de criação e status de bloqueio.
5. Um card deve seguir a ordem das colunas no board sem pular etapas, exceto pela coluna de cancelamento.
6. Se um card estiver bloqueado, ele não pode ser movido até ser desbloqueado.
7. Para bloquear um card, deve-se informar o motivo do bloqueio; para desbloqueá-lo, deve-se informar o motivo do desbloqueio.

### Menu de Manipulação do Board Selecionado

1. O menu deve permitir mover um card para a próxima coluna, cancelar um card, criar um card, bloqueá-lo e desbloqueá-lo.

## Database
![QuickDBD-NAME NOT SET (1)](https://github.com/user-attachments/assets/f2eea560-0f8e-43db-8547-b27cdae3401e)
