package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 8
 * Un codeur/décodeur par plages. 
 * @author Léo Bouraux (257368)
 *
 */
public final class RunLengthEncoder {
    private static final int MAX_BYTE_REPETITION = 130;

    private RunLengthEncoder() {
    }

    /**
     * @param l (Une liste d'octet)
     * @return la liste codée par plages
     */
    public static List<Byte> encode(List<Byte> l) {
        List<Byte> encodedList = new ArrayList<Byte>();
        byte currentByte = l.get(0);
        int currentByteCount = 0;
        for (Byte byte1 : l) {
            if (byte1 < 0) 
                throw new IllegalArgumentException();
            if (byte1.equals(currentByte) && currentByteCount < MAX_BYTE_REPETITION) {
                currentByteCount++;
            }
            else {
                if(currentByteCount==1) {
                    encodedList.add(currentByte);
                }
                else if(currentByteCount==2) {
                    encodedList.add(currentByte);
                    encodedList.add(currentByte);
                }
                else {
                    encodedList.add((byte) (2-currentByteCount));
                    encodedList.add(currentByte);
                }
                currentByte = byte1;
                currentByteCount = 1;
            }
        }
        // cas du dernier bit de la liste
        if(currentByteCount==1) {
            encodedList.add(currentByte);
        }
        else if(currentByteCount==2) {
            encodedList.add(currentByte);
            encodedList.add(currentByte);
        }
        else {
            encodedList.add((byte) (2-currentByteCount));
            encodedList.add(currentByte);
        }

        return Collections.unmodifiableList(encodedList);
    }
    
    /**
     * @param l (Une liste d'octets)
     * @return la liste décodée par plages 
     */
    public static List<Byte> decode(List<Byte> l) {
        List<Byte> decodedList = new ArrayList<>();
        if (l.get(l.size()-1)<0) 
            throw new IllegalArgumentException();
        int count = 1;
        for (Byte byte1 : l) {
            if(byte1 < 0) {
                count = 2 - byte1;
            }
            else {
                for(int i = 0 ; i<count ; i++) {
                    decodedList.add(byte1);
                }
                //remet le compteur à 1 si il y a un bit non compressé après
                count = 1;
            }
        }
        
        return Collections.unmodifiableList(decodedList);

    }
}
