import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.DecimalFormat;


public class ContaBancaria {
    protected int idConta;
    protected String nomePessoa;
    protected String cpf;
    protected String cidade;
    protected short transferenciasRealizadas;
    protected float saldoConta;
    DecimalFormat df = new DecimalFormat("#,##0.00");

    public ContaBancaria(int idConta, String nomePessoa, String cpf,
                         String cidade, short transferenciasRealizadas, float saldoConta){

        this.idConta = idConta;
        this.nomePessoa = nomePessoa;
        this.cpf = cpf;
        this.cidade = cidade;
        this.transferenciasRealizadas = transferenciasRealizadas;
        this.saldoConta = saldoConta;
    }

    public ContaBancaria(int idConta, String nomePessoa, String cpf,
                         String cidade, float saldoConta){

        this.idConta = idConta;
        this.nomePessoa = nomePessoa;
        this.cpf = cpf;
        this.cidade = cidade;
        this.transferenciasRealizadas = 0;
        this.saldoConta = saldoConta;
    }

    public ContaBancaria(){

        this.idConta = -1;
        this.nomePessoa = "";
        this.cpf = "";
        this.cidade = "";
        this.transferenciasRealizadas = 0;
        this.saldoConta = 0F;
    }

    public String toString(){
        return "--------------------\n ID Conta Bancária : " + idConta +
               "\n Nome Cliente: " + nomePessoa +
               "\n CPF: " + cpf +
               "\n Cidade: " + cidade +
               "\n Transferências Realizadas: " + transferenciasRealizadas +
               "\n Saldo: R$ " + df.format(saldoConta) +
               "\n--------------------";
    }

    public byte[] toByteArray() throws IOException {
        /*
        * Gera array de bytes na estrutura do objeto Conta Bancária.
        * */

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idConta);
        dos.writeUTF(nomePessoa);
        dos.writeUTF(cpf);
        dos.writeUTF(cidade);
        dos.writeShort(transferenciasRealizadas);
        dos.writeFloat(saldoConta);

        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException{
        /*
        * Recebe dados de um array de bytes para alimentar o objeto de Conta Bancária.
        * */

        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idConta = dis.readInt();
        nomePessoa = dis.readUTF();
        cpf = dis.readUTF();
        cidade = dis.readUTF();
        transferenciasRealizadas = dis.readShort();
        saldoConta = dis.readFloat();
    }
}
