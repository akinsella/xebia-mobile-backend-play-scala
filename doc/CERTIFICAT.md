xebia-mobile-backend-play-scala
===============================


#Création du KeyStore contenant votre clé privée et votre certificat APNS

Si vous vous apprêtez à utiliser les serveurs APNS (Apple Push Notification Service). Si les erreurs _SSL handshake failure, Received fatal alert: certificate_unknown, No X509TrustManager implementation available_ vous disent quelque chose, ou bien même vous font actuellement suer à grosses goutes, alors cet article est pour vous!

Comment on dit: Il n'y en a jamais assez ! Les différents tutoriels disponibles sur internet sont assez souvent incomplets… Très précis sur certains points de moindre importance, et d'un coup assez flous concernant certains points cruciaux. 

Nous allons tenter de voir ici comment assembler l'ensemble des pièces du puzzle pour être en mesure de discuter avec les serveurs d'Apple.

Pour communiquer avec les serveurs APNS d'Apple vous avez besoin de 2 fichiers: 

- Un fichier XebiaApns.p12 qui contient un export de votre certificat APNS, et de votre clé privée.
- Un fichier jssecacerts qui contient les certificats des serveurs sandbox gateway et feedback d'Apple. Ce fichier est utile pour faire connaître à l'implémentation SSL du JDK Java les certificats trustés.

#Pré-requis
Il est important de bien valider avec de démarrer les pré-requis demandés, sous peine de ne pouvoir se connecter avec succès aux serveurs d'Apple.

Vous devez vous assurer d'avoir à disposition les pièces suviantes à disposition:

- Avoir un mac à disposition (Cette article utilise Keychain disponible uniquement sur Mac)
- Votre clé privée et votre certificat de développeur Apple. Si vous avez un compte de développeur, mais pas encore de certificat, ni de paire de clé privée / public, vous pouvez suite le tutorial suivant qui vous permettra de checker de point: [Apple Push Notification Services Tutorial: Part 1/2](http://www.raywenderlich.com/3443/apple-push-notification-services-tutorial-part-12).


# Avant de démarrer

Pour vous connecter aux serveurs d'Apple, vous aurez besoin de deux fichiers: 

- Un premier fichier nommé: _jssecacerts_, puis,
- Un second fichier nommé: _xebia_apns.p12_.

#Création du fichier xebia_apns.p12

Si vous avez correctement installé vos certificats de développeur Apple et d'accès aux serveurs APNS, vous devez avoir à disposition dans _Keychain_ quelquechose qui ressemble à ça:


Pour générer le fichier _xebia_apns.p12_, vous devez sélectionner votre clé privée, ainsi que le certificat apns de développement, faire un clic droit et choisir: "exporter". Vous aurez alors le choix d'exporter ces deux fichiers au format _p12_.

#Création du fichier jssecacerts

2 certificats doivent être trustés par la JVM avant de pouvoir communiquer avec les serveurs APNS. Il faut donc installer les certificat de l'URL de Gateway et de l'url de Feedback.

Vous avez deux possibilités: vous pouvez soit récupérer un truststore déjà préparé ici:

- [https://github.com/notnoop/java-apns/wiki]() ( [http://cloud.github.com/downloads/notnoop/java-apns/jssecacerts]() )

Ou bien vous pouvez suivre la procédure de création du truststore ci-dessous. Cela peut s'avérer nécessaire si les certificats ont été renouvelés… Un explicatif est présent sur le GitHub du connecteur APNS pour Java: java-apns ( [https://github.com/notnoop/java-apns/wiki]() )

Le fichier généré est à installer dans le dossier un dossier de l'application. Le dossier cert par exemple.


## Programme d'installation
Un programme d'installation disponible à l'origine sur feu le site de Sun est maintenant disponible sur le site d'Atlassian. Vous pouvez le récupérer à l'URL suivante: 

	wget https://confluence.atlassian.com/download/attachments/180292346/InstallCert.java?version=1&modificationDate=1315453596921
	
_Tip:_ Ce programme est disponible sur de nombreux sites web via une recherche Google.


### Compilation du code programme d'installation des certificats

Dans le dossier _cert_ exécuter la commande: 

	javac InstallCert.java

### Création d'un keystore

Lors de la première exécution, un keystore nommé jssecacerts sera créé avec le mot de passe 'changeit'. Le logiciel porteclé permettra de changer le mot de passe à la valeur souhaitée.
	
### Installation des certificats de l'url Gateway

Exécuter la commande suivante et choisir le premier certificat: 

	java InstallCert gateway.sandbox.push.apple.com:2195 <SOME_PASSWORD>

### Installation des certificats de l'url Gateway
Exécuter la commande suivante et choisir le premier certificat: 

	java InstallCert feeback.sandbox.push.apple.com:2196 <SOME_PASSWORD>
	
### Portecle
L'outil portecle permet de vérifier votre keystore. Vous pouvez le télécharger à l'adresse suivante: 

	http://sourceforge.net/projects/portecle/files/latest/download
	
_Tip:_ Ne pas oublier de sauver dans l'application porteclé à chaque modification.
	

#Execution du programme avec les certificats

	play debug -Djavax.net.debug=all -Djavax.net.ssl.trustStore=cert/jssecacerts -Djavax.net.ssl.trustStorePassword=<SOME_PASSWORD>


#Accepter les Certificats auto signés

	http://www.howardism.org/Technical/Java/SelfSignedCerts.html


#Sus aux légendes urbaines !

- Non, il n'est pas nécessaire d'utiliser Bouncy Castle, le JDK contient tout ce qu'il faut pour communiquer avec les serveurs Apple.
- Non, il n'est pas nécessaire d'installer quelques fichier que ce soit dans le dossier lib/security de la JVM. Vous pouvez tout configurer via des propriétés systèmes, soit au lancement de la JVM, soit directement dans le code.
- Aucun X509TrustManager n'est à ajouter à votre code



#Extrait du code à utilisé pour communiquer avec les serveurs Apple

Il existe plusieurs librairies Java qui permettent de communiquer avec les serveurs d'Apple:

- javapns, [http://code.google.com/p/javapns/]()
- java-apns, [https://github.com/notnoop/java-apns]()

Ayant déjà utilisé la seconde librairie pour construire le composant [camel-apns](http://camel.apache.org/apns.html), je vous propose ci dessous, un programme d'exemple qui vous permettra de tester rapidement si vous avez fait les choses correctement pour vous permettre de communiquer avec les serveurs Apple.


pom.xml:

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <groupId>fr.xebia</groupId>
        <artifactId>java-apns-test</artifactId>
        <version>1.0.0-SNAPSHOT</version>


        <dependencies>
            <dependency>
                <groupId>com.notnoop.apns</groupId>
                <artifactId>apns</artifactId>
                <version>0.1.6</version>
            </dependency>
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.8.2</version>
            </dependency>
        </dependencies>

    </project>

JavaApnsTest.java:


    package fr.xebia;
    
    import com.notnoop.apns.*;
    import org.junit.Test;
    
    import java.io.IOException;
    import java.security.*;
    import java.security.cert.CertificateException;
    
    import static java.lang.String.format;
    
    public class JavaApnsTest {
    
        @Test
        public void testApns() throws IOException, KeyManagementException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, UnrecoverableKeyException {
    
            System.setProperty("javax.net.debug", "all");
            System.setProperty("javax.net.ssl.trustStore", "cert/jssecacerts");
            System.setProperty("javax.net.ssl.trustStorePassword", "jssecacerts_password");
    
    
    
            ApnsService apnsService = APNS.newService()
                .withCert("cert/Xebia.p12", "<xebia_p12_password>")
                .withDelegate(new ApnsDelegate() {
                    public void messageSent(ApnsNotification notification) {
                        System.out.println(format("Notification send: %s", notification));
                    }
    
                    public void connectionClosed(DeliveryError deliveryError, int p2) {
                        System.out.println(format("Delivery error[%s]: %s", p2, deliveryError));
                    }
    
                    public void messageSendFailed(ApnsNotification notification, Throwable throwable) {
                        System.out.println(format("Could not send notification: '%s' - Notification: %s", throwable.getMessage(), notification));
                    }
                })
                .withReconnectPolicy(ReconnectPolicy.Provided.EVERY_HALF_HOUR)
                .withSandboxDestination()
                .build();
    
            String payload = APNS.newPayload().alertBody("Some message").shrinkBody(" ...").build();
    
            apnsService.push(new SimpleApnsNotification("AFEFFBC9825671AD354B1C53", payload));
        }
    
    }


# Liens utiles

La lecture des liens suivants est certe utile, mais à prendre avec des pincettes, puisque souvent imprécise!

- [http://code.google.com/p/javapns/wiki/GetAPNSCertificate]()
- [http://code.google.com/p/javapns/wiki/How2UseJavapns]()
- [http://developer.apple.com/library/mac/#documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/ProvisioningDevelopment/ProvisioningDevelopment.html#//apple_ref/doc/uid/TP40008194-CH104-SW3]()



