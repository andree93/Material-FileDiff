# Material-FileDiff

Applicazione per la verifica del Checksum di file singoli o in Batch.
L'applicazione si divide in due parti (al momento solo 2 sono implementate): 
Calcolo del checksum di un singolo file.
Calcolo del checksum di più file ed esportazione dei risultati in JSON.
Verifica della corrispondenza del checksum dei file selezionati con i valori caricati da un file JSON precedentemente generato (Implementato, ma da migliorare. Al momento non mostra i dettagli circa i file mancanti e quali file risultano diversi).
L'applicazione supporta le versioni di Android a partire dalla versione 7.0 (Nougat).
Sono utilizzate le librerie:
RXJava2
Apache Commons codec
androidx.lifecycle viewmodel
androidx.documentfile
