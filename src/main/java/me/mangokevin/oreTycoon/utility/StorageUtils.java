package me.mangokevin.oreTycoon.utility;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StorageUtils {
    public static byte[] toByteArray(Inventory inventory) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            BukkitObjectOutputStream objectOutputStream = new BukkitObjectOutputStream(outputStream);

            //Anzahl an Items die kommen
            objectOutputStream.writeInt(inventory.getSize());

            //Jedes Item nacheinander zerlegen
            for (int i = 0; i < inventory.getSize(); i++) {
                objectOutputStream.writeObject(inventory.getItem(i));
                Console.log("Writing item at position " + i);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fromByteArray(byte[] bytes, Inventory inventory) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            BukkitObjectInputStream objectInputStream = new BukkitObjectInputStream(inputStream);

            int size = objectInputStream.readInt();
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, (ItemStack) objectInputStream.readObject());
                Console.log("Reading item at position " + i);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
