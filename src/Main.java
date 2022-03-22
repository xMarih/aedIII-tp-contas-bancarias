import java.io.EOFException;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.io.EOFException;
import java.util.Scanner;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args){
        Scanner menu;
        RandomAccessFile db;
        String nomePessoa, cpf, cidade;
        ContaBancaria cbNovo;
        int id;
        float saldo;
        byte opcao;

        try{
            // Inicializa vari√°veis de controle
            opcao = 0;
            menu = new Scanner(System.in);
            db = new RandomAccessFile("contas.db", "rw");
            db.setLength(0);

            // Inicializa arquivo gravando o cabe√ßalho
            db.writeInt(0);

            do {
                // Print menu principal
                System.out.println("\n#### Sistema de Gest„o Banc·ria - SGB ####");
                System.out.println("\n-----------------------------");
                System.out.println("| OpÁ„o 1 - Cadastrar Conta Banc·ria    |");
                System.out.println("| OpÁ„o 2 - Realizar TransferÍcia      |");
                System.out.println("| OpÁ„o 3 - Buscar Conta                 |");
                System.out.println("| OpÁ„o 4 - Atualizar Conta              |");
                System.out.println("| OpÁ„o 5 - Deletar Conta                |");
                System.out.println("| OpÁ„o 0 - Encerrar Programa            |");
                System.out.println("-----------------------------\n");
                System.out.print("Digite uma OpÁ„o: ");

                opcao = menu.nextByte();
                menu.nextLine();

                switch (opcao) {
                    case 1:
                        System.out.print("\n -- Cadastrar Conta Banc·ria -- \n");

                        // Leitura dos dados do cliente que ser√° cadastrados
                        System.out.print("\n-> Nome do cliente: ");
                        nomePessoa = menu.nextLine();
                        System.out.print("-> CPF do cliente: ");
                        cpf = menu.nextLine();
                        System.out.print("-> Cidade do cliente: ");
                        cidade = menu.nextLine();
                        System.out.print("-> Saldo inicial do cliente: ");
                        saldo = menu.nextFloat();

                        CadastrarContaBancaria(nomePessoa, cpf, cidade, saldo, db);
                        break;

                    case 2:
                        System.out.print("\n -- Realizar TransferÍncia -- \n");

                        // Leitura dos dados dos clientes que participaram da transfer√™ncia
                        System.out.print("\n-> ID do cliente 1 (Conceder valor): ");
                        int idConta1 = menu.nextInt();
                        menu.nextLine();
                        System.out.print("-> ID do cliente 2 (Receber valor): ");
                        int idConta2 = menu.nextInt();
                        menu.nextLine();
                        System.out.print("-> Valor da transferÍncia: ");
                        float valor = menu.nextFloat();

                        RealizarTransferencia(idConta1, idConta2, valor, db);
                        break;

                    case 3:
                        System.out.print("\n -- Buscar Conta -- \n");

                        // Leitura dos dados do cliente que ser√° buscado
                        System.out.print("\n-> ID do cliente: ");
                        id = menu.nextInt();

                        BuscarConta(id, db);
                        break;

                    case 4:
                        System.out.print("\n -- Atualizar Conta -- \n");

                        // Leitura dos dados do cliente que ser√° atualizado
                        System.out.print("\n-> ID do cliente: ");
                        id = menu.nextInt();
                        menu.nextLine();
                        System.out.print("-> Nome do cliente: ");
                        nomePessoa = menu.nextLine();
                        System.out.print("-> CPF do cliente: ");
                        cpf = menu.nextLine();
                        System.out.print("-> Cidade do cliente: ");
                        cidade = menu.nextLine();
                        System.out.print("-> Saldo inicial do cliente: ");
                        saldo = menu.nextFloat();

                        // Cria objeto novo registro
                        cbNovo = new ContaBancaria(id, nomePessoa, cpf, cidade, saldo);

                        AtualizarConta(cbNovo, db);
                        break;

                    case 5:
                        System.out.print("\n -- Deletar Conta -- \n");

                        // Leitura dos dados do cliente que ser√° deletado
                        System.out.print("\n-> ID do cliente: ");
                        id = menu.nextInt();

                        DeletarConta(id, db);
                        break;

                    case 0:
                        System.out.println("\nAtÈ logo!\n");
                        break;

                    default:
                        System.out.print("\nOpÁ„o Inv·lida!");
                        break;
                }
            } while (opcao != 0);

            db.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void CadastrarContaBancaria(String nomePessoa, String cpf,
                                              String cidade, Float saldoInicial, RandomAccessFile fout) throws IOException{

        int ultimoId, proximoId;
        byte ba[];

        // Ler pr√≥ximo id do cabe√ßalho
        fout.seek(0);
        ultimoId = fout.readInt();
        proximoId = ultimoId + 1;

        // Escrever novo id no cabe√ßalho
        fout.seek(0);
        fout.writeInt(proximoId);

        // Cria objeto para o registro
        ContaBancaria cb = new ContaBancaria(ultimoId, nomePessoa, cpf, cidade, (short)0, saldoInicial);

        // Cria registro para o objeto criado e move para o fim do arquivo
        ba = cb.toByteArray();
        fout.seek(fout.length());

        // Escreve registro
        fout.writeByte(0); // L√°pide: 0 -> Ativo | 1 -> Removido
        fout.writeInt(ba.length); // Tamanho do registro
        fout.write(ba); // Registro pr√≥priamente dito

        // Confirma√ß√£o de cadastro com sucesso
        System.out.println("\nCliente registrado com sucesso!\n");
        System.out.println(cb);
    }

    public static void RealizarTransferencia(int idConta1, int idConta2, float valor, RandomAccessFile fout) throws IOException{
        boolean flag1, flag2;
        byte lapide;
        int tamRegistro;
        long posConta1, posConta2;
        byte ba[];
        ContaBancaria cb1, cb2, cb;

        // Inicializa vari√°veis
        DecimalFormat df = new DecimalFormat("#,##0.00");
        posConta1 = -1;
        posConta2 = -1;
        flag1 = false;
        flag2 = false;
        cb = new ContaBancaria();
        cb1 = new ContaBancaria();
        cb2 = new ContaBancaria();

        // Move ponteiro para in√≠cio do arquivo ap√≥s o cabe√ßalho
        fout.seek(0);
        fout.readInt();

        // Loop por todos registros do arquivo
        while(true){
            try{
                lapide = fout.readByte();
                tamRegistro = fout.readInt();

                // Verifica tamanhos
                if(!flag1) posConta1 = fout.getFilePointer();
                if(!flag2) posConta2 = fout.getFilePointer();

                // Ler registro
                ba = new byte[tamRegistro];
                fout.read(ba);
                cb.fromByteArray(ba);

                // Verificar l√°pide
                if(lapide == 0){
                    // Armazena valores de ambos os registros
                    if(cb.idConta == idConta1){
                        flag1 = true;
                        cb1.fromByteArray(ba);
                    }
                    else if(cb.idConta == idConta2){
                        flag2 = true;
                        cb2.fromByteArray(ba);
                    }
                }

                if(flag1 && flag2) break;
            }
            catch(EOFException err){
                break;
            }
        }

        if(!(flag1 && flag2)){
            System.out.println("\nCliente n„o encontrado!\n");
        }
        else{
            // Atualiza saldo e transfe√™ncia de ambas as contas
            cb1.saldoConta = cb1.saldoConta - valor;
            cb2.saldoConta = cb2.saldoConta + valor;
            cb1.transferenciasRealizadas++;
            cb2.transferenciasRealizadas++;

            // Escreve os registros atualizados
            fout.seek(posConta1);
            fout.write(cb1.toByteArray());
            fout.seek(posConta2);
            fout.write(cb2.toByteArray());

            System.out.println("\nTransferÍncia realizada com sucesso!");
            System.out.println("De " + cb1.nomePessoa + " no valor de R$ " + df.format(valor) + " para " + cb2.nomePessoa + ".");
        }
    }

    public static void BuscarConta(int id, RandomAccessFile fin) throws IOException{
        byte lapide;
        int tamRegistro;
        byte ba[];
        boolean flagEncontrado = false;
        ContaBancaria cb = new ContaBancaria();

        // Move ponteiro para in√≠cio do arquivo ap√≥s o cabe√ßalho
        fin.seek(0);
        fin.readInt();

        // Loop por todos registros do arquivo
        while(true){
            try{
                lapide = fin.readByte();
                tamRegistro = fin.readInt();

                // Ler registro
                ba = new byte[tamRegistro];
                fin.read(ba);
                cb.fromByteArray(ba);

                // Verificar l√°pide
                if(lapide == 0 && cb.idConta == id){
                    flagEncontrado = true;
                    System.out.println(cb);
                    break;
                }
            }
            catch(EOFException err){
                break;
            }
        }

        if(!flagEncontrado){
            System.out.println("\nCliente n„o encontrado!\n");
        }
    }

    public static void AtualizarConta(ContaBancaria cbNovo, RandomAccessFile fout) throws IOException{
        long pos;
        byte lapide;
        int tamVelhoRegistro;
        byte ba[];
        boolean flagEncontrado = false;
        byte cbNovoRegistro[];
        ContaBancaria cbVelho = new ContaBancaria();

        // Move ponteiro para in√≠cio do arquivo ap√≥s o cabe√ßalho
        fout.seek(0);
        fout.readInt();

        while(true){
            try{
                pos = fout.getFilePointer();
                lapide = fout.readByte();
                tamVelhoRegistro = fout.readInt();

                // Ler registro
                ba = new byte[tamVelhoRegistro];
                fout.read(ba);
                cbVelho.fromByteArray(ba);

                if(lapide == 0 && cbVelho.idConta == cbNovo.idConta){
                    flagEncontrado = true;

                    // Monta registro conservando valor de transa√ß√µes
                    cbNovo.transferenciasRealizadas = cbVelho.transferenciasRealizadas;
                    cbNovoRegistro = cbNovo.toByteArray();

                    // Verificar tamanho do novo registro
                    if(cbNovoRegistro.length <= tamVelhoRegistro){
                        // Escrever na mesma posi√ß√£o mantendo indicador de tamanho
                        fout.seek(pos);

                        // Mover para in√≠cio do registro
                        fout.readByte();
                        fout.readInt();

                        fout.write(cbNovoRegistro);
                    }
                    else{
                        // Marcar registro como exclu√≠do
                        fout.seek(pos);
                        fout.writeByte(1);

                        // Reescrever no fim do arquivo o registro atualizado
                        fout.seek(fout.length());
                        fout.writeByte(0);
                        fout.writeInt(cbNovoRegistro.length);
                        fout.write(cbNovoRegistro);
                    }

                    System.out.println("\nCliente atualizado com sucesso!");
                    System.out.println(cbNovo);

                    break;
                }


            }
            catch(EOFException err){
                break;
            }
        }

        if(!flagEncontrado){
            System.out.println("\nCliente n„o encontrado!\n");
        }
    }

    public static void DeletarConta(int id, RandomAccessFile fout) throws IOException{
        byte lapide;
        int tamRegistro;
        long pos;
        byte ba[];
        boolean flagEncontrado = false;  
        ContaBancaria cb = new ContaBancaria();

        // Move ponteiro para in√≠cio do arquivo ap√≥s o cabe√ßalho
        fout.seek(0);
        fout.readInt();

        // Loop por todos registros do arquivo
        while(true){
            try{
                pos = fout.getFilePointer();
                lapide = fout.readByte();
                tamRegistro = fout.readInt();

                // Ler registro
                ba = new byte[tamRegistro];
                fout.read(ba);
                cb.fromByteArray(ba);

                // Verificar l√°pide
                if(lapide == 0 && cb.idConta == id){
                    flagEncontrado = true;

                    // Mudar l√°pide
                    fout.seek(pos);
                    fout.writeByte(1);

                    System.out.println("\nCliente removido com sucesso!");
                    System.out.println(cb);

                    break;
                }
            }
            catch(EOFException err){
                break;
            }
        }

        if(!flagEncontrado){
            System.out.println("\nCliente n„o encontrado!\n");
        }
    }
}
