package test;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import component.Accounts;
import component.Clients;
import component.Credit;
import component.CurrentAccount;
import component.Debit;
import component.Flow;
import component.SavingsAccount;
import component.Transfert;

public class Main {
	static List<Accounts> accounts = new ArrayList<>();

	public static void main(String[] args) {
		List<Clients> clients;
		HashMap<Integer, Accounts> map;
		List<Flow> flows;
		
		//Genère trois clients
		System.out.println("Clients dans la List Clients");
		clients = generateClients(3);
		showClients(clients);
		System.out.println();
		
		//Génère les comptes associés aux clients
		System.out.println("Comptes : ");
		accounts = generateAccount(clients);
		showAccounts(accounts);
		System.out.println();
		
		//mets à jour la table, avec comme index, l'id des comptes
		map = generateHashTable(accounts);
		System.out.println("Hashmap avant mouvements: ");
		showHashMap(map);
		
		//Genère les mouvements financiers demandés, en dur, et update les comptes
		flows = generateFlows();
		updateAccounts(map,flows);
		System.out.println();
		
		//Montre la map triée sur les balances, de façon croissante après mouvements
		System.out.println("Hashmap après mouvements: ");
		showHashMap(map);
		System.out.println();
		
		//Récupère d'autres mouvements d'un fichier json, update les comptes
		loadJson(flows);		
		updateAccounts(map,flows);
		System.out.println("Hashmap après mouvement du JSON");
		showHashMap(map);
		System.out.println();
		
		//Récupère d'autres comptse clients d'un fichier XML 
		concatList(generateAccount(loadXML()));
		System.out.println("\nNouveau comptes et anciens réunis");
		showAccounts(accounts);
		System.out.println("\nMise à jour de la hashmap triée sur balance");
		map = generateHashTable(accounts);
		showHashMap(map);
	}
	
	/*
	 * Fonction qui charge un fichier XML de clients
	 * Retourne une liste de client 
	 */
	private static List<Clients> loadXML() {
		List<Clients> clients = new ArrayList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File("src/ressources/accounts.xml")); // récupère le xml
						 
			NodeList nList = document.getElementsByTagName("client"); // récupère les clients
			 
			for (int temp = 0; temp < nList.getLength(); temp++) { // extrait les clients pour les mettre dans la list
				Node node = nList.item(temp);
				if (node.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element) node;
					clients.add(new Clients(eElement.getElementsByTagName("name").item(0).getTextContent(), eElement.getElementsByTagName("firstname").item(0).getTextContent()));
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {	
			e.printStackTrace();
		}
		return clients;
	}

	/*
	 * Parse un fichier par Path, JSON, dans un objet manipulable
	 */
	public static JSONObject parseJSONFile(Path path) throws IOException {
		String content = new String(Files.readAllBytes(path));
	    return new JSONObject(content);
	}
	/*
	 * Concatène des lists d'accounts
	 */
	public static void concatList(List<Accounts> acc) {
		for(int i = 0 ; i < acc.size() ; i++)
			accounts.add(acc.get(i));
	}

	/*
	 * charge le fichier JSON de mouvements financiers
	 * Mets à jour la List flows
	 */
	private static void loadJson(List<Flow> flowtofill) {
		Path path = Paths.get("src/ressources/flows.json");
		try {
			JSONObject obj = parseJSONFile(path);
			JSONArray flows = obj.getJSONArray("flows");
			for (int i = 0; i < flows.length(); i++) {
			    JSONObject jsonobject = flows.getJSONObject(i);
			    String comment = jsonobject.getString("comment");
			    Double amount = jsonobject.getDouble("amount");
			    int target = jsonobject.getInt("target");
			    if(comment.contains("transfert")) {
			    	int source = jsonobject.getInt("source");
			    	flowtofill.add(new Transfert(comment,amount,(Integer)target, false,LocalDateTime.now().plus(2,ChronoUnit.DAYS),(Integer)source));
			    } 
			    else if(comment.contains("debit"))
			    	flowtofill.add(new Debit(comment,amount,(Integer)target, false,LocalDateTime.now().plus(2,ChronoUnit.DAYS)));
			    else if(comment.contains("credit"))
			    	flowtofill.add(new Credit(comment,amount,(Integer)target, false,LocalDateTime.now().plus(2,ChronoUnit.DAYS)));
			}
			
		} catch (IOException ex) {

		  ex.printStackTrace();//handle exception here
		}
	}

	/*
	 * Mets à jour les comptes selon les mouvements qui dont l'effet est à false
	 * Une fois qu'un mouvement est traité, met son effet à true
	 * Affiche si un compte est dans le négatif
	 */
	private static void updateAccounts(HashMap<Integer, Accounts> map, List<Flow> flows) {
		for(Flow f : flows) {
			if(f instanceof Transfert) {
				map.get(f.getTargetAccId()).setBalance(f);
				map.get(((Transfert) f).getSource()).setBalance(f);
			}
			else
				map.get(f.getTargetAccId()).setBalance(f);
			f.setEffect(true);
		}
		filterAccounts(map, isNegative()).forEach(e -> System.out.println("compte negatif :"+e.toString()));
		
	}
	/*
	 * Est le prédicat pour gérer les comptes passés en négatifs
	 */
	public static Predicate<Accounts> isNegative() {
		return account -> account.getBalance() < 0;
	}
	
	/*
	 * Est le filtre utilisé dans updateAccounts pour voi les comptes en négatifs 
	 */
	public static List<Accounts> filterAccounts(HashMap<Integer, Accounts> map, Predicate<Accounts> predicate) {
		List<Accounts> listacc = new ArrayList<>(map.values());
		return listacc.stream().filter(predicate).collect(Collectors.toList());
	}
	
	/*
	 * Génère les mouvements en dur
	 * Retourne la List de mouvements associés
	 */
	private static List<Flow> generateFlows() {
		List<Flow> flows= new ArrayList<>();
		flows.add(new Debit("debit", 50.0, 1, false, LocalDateTime.now().plus(2,ChronoUnit.DAYS)));
		for(Accounts a : accounts ) 
			if(a instanceof CurrentAccount)
				flows.add(new Credit("credit", 100.50, a.getAccountNumber(), false, LocalDateTime.now().plus(2,ChronoUnit.DAYS)));
		for(Accounts a : accounts ) 
			if(a instanceof SavingsAccount)
				flows.add(new Credit("credit", 1500, a.getAccountNumber(), false, LocalDateTime.now().plus(2,ChronoUnit.DAYS)));
		flows.add(new Transfert("transfert", 50, 2, false, LocalDateTime.now().plus(2,ChronoUnit.DAYS),1	));
		return flows;
	}

	/*
	 * Affiche la hashmap Classée par Balance, en ordre croissant
	 */
	private static void showHashMap(HashMap<Integer, Accounts> hashtable) {
		List<Map.Entry<Integer, Accounts>> sortedEntries = new ArrayList<>( hashtable.entrySet() );
		Collections.sort(sortedEntries, Map.Entry.comparingByValue(Comparator.comparingDouble(Accounts::getBalance)
		            .thenComparingDouble(Accounts::getBalance)));
		
		sortedEntries.stream().forEach(e -> System.out.println(e.toString()));
	}

	/*
	 * Génère la hashmap
	 */
	private static HashMap<Integer, Accounts> generateHashTable(List<Accounts> accounts){
		HashMap<Integer, Accounts> hashmap = new HashMap<>();
		for(int i = 0 ; i < accounts.size() ; i++)
			hashmap.put(accounts.get(i).getAccountNumber(), accounts.get(i));
		return hashmap;
	}
	
	/*
	 * Montre les accounts
	 */
	private static void showAccounts(List<Accounts> accounts) {
		accounts.stream().forEach(e -> System.out.println(e.toString()));
	}

	/*
	 * Génère une liste de comptse depuis une liste de clients
	 */
	private static List<Accounts> generateAccount(List<Clients> clients) {
		List<Accounts> accountToFill = new ArrayList<>();
		int taille=accounts.size()/2;
		
		for(int i = 0 ; i < clients.size() ; i ++) {
			accountToFill.add(new CurrentAccount("CurrentAccount"+(i+taille), clients.get(i)));
			accountToFill.add(new SavingsAccount("SavingsAccount"+(i+taille), clients.get(i)));
		}
		return accountToFill;
	}

	/*
	 * Génère une liste de client depuis un simple nombre ; 
	 * Les noms et prénoms seront générés de la façon suivante :
	 *  - name[i] - firstname[i]
	 */
	public static List<Clients> generateClients(int nbclients) {
		List<Clients> clienttofill = new ArrayList<>();
		for(int i = 0; i < nbclients; i++)
			clienttofill.add(new Clients("name"+i, "firstname"+i));
		return clienttofill;
	}

	/*
	 * Affiche la liste des clients
	 */
	public static void showClients(List<Clients> listclient) {
		listclient.stream().forEach(e -> System.out.println(e.toString()));
	}

}
