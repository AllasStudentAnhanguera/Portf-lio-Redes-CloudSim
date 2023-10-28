
package redes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import static org.cloudbus.cloudsim.examples.power.Helper.createBroker;
import static org.cloudbus.cloudsim.examples.power.Helper.printCloudletList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 *
 * @author Allas
 */
public class Redes {
    
    // CloudLista
    /*
    Lista criada para utilização do Cloudlet, Cloudlet é um modelo de pacotes 
    arquitetural de três camadas para fornecimento de recursos computacionais aos dispositivos 
    portáteis. Cria um elemento intermediário, responsável por garantir recursos com baixa latência 
    e maior confiabilidade.
    */
    private static List<Cloudlet> cloudletList;
    
    // Lista Vm
    /*
    Vm é um pacote que fornece implementação de Máquinas Virtuais (Vm) 
    que são pacote de software que emula a arquitetura de uma máquina física. Cada 
    Vm é executada por um Host e é utilizada para executar aplicações ( Cloudlet). 
    Tanto as VMs quanto os Cloudlets pertencem a um cliente de nuvem específico 
    ( representado por um DataCenterBroker).
    */
    private static List<Vm> vmlist;
    
    /*
    Na construção do main, usamos uma anotação para exclusão de avisos 
    de compilação, a anotação usada será a SuppressWarnings, veja abaixo como chamar:
    */
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        
        /*
        Dentro do main, chamamos os componentes do CloudSim através de variáveis.
        Chamamos uma breve apresentação usando mais um pacote do CloudSim que é o Logger, 
        é utilizado para realizar o registro de processos de simulação. 
        Importamos ele usando a seguinte linha de comando:
        import org.cloudbus.cloudsim.Log;
        Após a apresentação, criamos um try catch para darmos início ao chamado dos componentes e efetuar
        quaisquers tipos de tratamento de erros
        */
        
        Log.printLine("Iniciando o CloudSim...");
        
        try {
            // Criamos as variáveis que utilizaremos:
            int numero_usuario = 1; // número de usuários dentro da simulação
            /*
            Na variável Calendar, importamos o pacote Calendar do java. Fornece métodos 
            para converter entre instantes específicos no tempo e um conjunto de calendário, 
            como ano, mês, dia, hora e muito mais.
            Para chama-lo, usamos o comando:
            import java.util.Calendar;
            */
            Calendar calendario = Calendar.getInstance(); // usamos para marcar o início da simulação
            boolean sinalizador_de_rastreamento = false; // é um arquivo que usamos para gerar relatorios
            
            // Inicializando o CloudSim
            /*
            Iniciando o CloudSim.
            Para iniciar o CloudSim fazemos da seguinte maneira:
            CloudSiml.init();
            Dentro do colchetes, chamamos os componentes criados acima:
            numero_usuario, calendario, sinalizador_de_rastreamento
            Chamamos o seguinte comando:
            */
            CloudSim.init(numero_usuario, calendario, sinalizador_de_rastreamento);
            
            // Criando o DataCenter
            /*
            Data Center no CloudSim
            O Datacenter é uma interface a ser implementada por cada classe que fornece 
            recursos de Datacenter. A interface implementa o Null Object Design Pattern para começar 
            a evitar o NullPointerException ao usar o Null objeto em vez de atribuir null a Datacenter 
            as suas variáveis.
            Importamos da seguinte maneira para nosso projeto:
            import org.cloudbus.cloudsim.Datacenter;
            */
            /*
            Implementamos uma classe para utilizar o Datacenter dentro do nosso projeto.
            A classe será privada, statica, chamamos o Datacenter e damos o nome a classe 
            createDatacenter, como parâmetros usaremos String datacenter_0, que indicará nosso primeiro Datacenter.
            */
            Datacenter datacenter0 = createDatacenter("Datacenter_0");
            
            //  btoker / corretor
            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();
            
            // lista de maquina virtual
            vmlist = new ArrayList<Vm>();
            
            // VM descricao
            int vmid = 0;
            int mips = 1000;
            int size = 10000; // tamanho (MB)
            int ram = 512; // vm memoria (MB)
            long bw = 1000;
            int pesNumber = 1; // numero de cpus
            String vmm = "Xen"; // VMM nome
            
            // criando VM
            Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            
            // adicionando VM na vmList
            vmlist.add(vm);
            
            // envia listas VMs para o corretor
            broker.submitVmList(vmlist);
            
            // criando um Cloudlet
            cloudletList = new ArrayList<Cloudlet>();
            
            // Cloudlet propriedades
            int id = 0;
            long legth = 4000000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            
            Cloudlet cloudlet = 
                    new Cloudlet(id, legth, pesNumber, fileSize, 
                            outputSize, utilizationModel, utilizationModel, 
                            utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudlet.setVmId(vmid);
            
            // adicionando o cloudlet na lista
            cloudletList.add(cloudlet);
            
            // submetendo lista cloudlet ao corretor
            broker.submitCloudletList(cloudletList);
            
            // Iniciando simulacoes
            CloudSim.startSimulation();
            
            // Parando simulacoes
            CloudSim.stopSimulation();
            
            // imprimir resultados quando a simulacao terminar
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);
            
            Log.printLine("CloudSim Exemplo 1 finalizado!");
            
        }
        catch(Exception e) {
                e.printStackTrace();
                Log.printLine("Algo deu errado!");
        }
        
    }

    private static Datacenter createDatacenter(String name) {
        
        // Criando o Host dentro de uma lista 
        // Os Hosts são como máquinas virtuais ou seja, computadores reais. 
        // Porém, eles precisam ter núcleos, criaremos os núcleos loga abaixo do Host.
        List<Host> hostList = new ArrayList<Host>();
        
        // Criando o núcleo, para o Host do CloudSim, que significa os processadores
        List<Pe> peList = new ArrayList<Pe>();
        
        // é a quantidade de mips que cada processador irá processar.
        int mips = 1000;
        
        // criando e chamando os processadores e sua quantidade de mips que será processado.
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));
        
        // Creando os Hosts e suas caracteristicas como
        
        // id do host
        int hostId = 0;
        // memoria ram usada em cada host
        int ram = 2048;
        // armazenamento dos hosts
        long storage = 1000000;
        // nome do atributo usado para larga banda
        int bw = 10000;
        
        // adicionando a lista de host
        /*
        Adicionando a hostList.
        Para adicionar a hostList, usamos o comando .add() e dentro 
        colocamos nosso Host com seu componentes que faram parte do Host.
        */
        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram), // Define o provisionador de RAM com capacidade em Megabytes.
                        new BwProvisionerSimple(bw), // Define o provisionador de largura de banda (BW) com capacidade em Megabits/s.
                        storage,
                        peList,
                        new VmSchedulerTimeShared(peList)
                )
        );
        
        /*
        Crie um objeto DatacenterCharacteristics que armazena as propriedades
        de um data center: arquitetura do sistema operacional, lista de máquinas,
        política de alocação: tempo ou espaço compartilhado, cronograma e seu preço
        */
        String arch = "x86"; // arquitetura do sistema
        String os = "Linux"; // sistema operacional
        String vmm = "Xen"; // maquina virtual
        double time_zone = 10.0; // fuso horario em que este recurso esta localizado
        double cost = 3.0; // o custo de usar o processamento neste recurso
        double costPerMem = 0.05; // o custo de usar memória neste recurso
        double costPerStorage = 0.001; // o custo de usar armazenamento neste
        
        double costPerBw = 0.0; // o custo de usar bw neste recurso
        LinkedList<Storage> storageList = new LinkedList<Storage>();
        
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                        arch, os, vmm, hostList, time_zone, cost, costPerMem, 
                        costPerStorage, costPerBw);
        
        // Criando um objeto PowerDatacenter.
        Datacenter datacenter = null;
        
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, costPerBw);
        } catch (Exception e) {
                e.printStackTrace();
        }
        
        return datacenter;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static DatacenterBroker createBroker() {
            DatacenterBroker broker = null;
            try {
                    broker = new DatacenterBroker("Broker");
            } catch (Exception e) {
                    e.printStackTrace();
                    return null;
            }
            return broker;
    }
    
    /**
     * Imprime os objetos Cloudlet.
     * @param list list de Cloudlets
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        
        // método indent para adicionar ou remover espaços em branco no início da linha para ajudar recuo de cada linha da string.
        String indent = "       ";
        Log.printLine();
        Log.printLine("========== SAIDA DO PROGRAMA ==========");
        Log.printLine("Cloudlet ID"+ indent + "STATUS" + indent
                        + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                        + "Start Time" + indent + "Finish Time");
        
        // DecimalFormat é usado como método padrão de formatação de símbolos 
        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);
            
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCESSO");
                
                Log.printLine(indent + indent + cloudlet.getResourceId()
                                + indent + indent + indent + cloudlet.getVmId()
                                + indent + indent
                                + dft.format(cloudlet.getActualCPUTime()) + indent
                                + indent + dft.format(cloudlet.getExecStartTime())
                                + indent + indent
                                + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
    
}
