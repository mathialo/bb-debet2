/*
 * Copyright (C) 2019  Mathias Lohne
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mathiaslohne.bbdebet2.kernel.core;

import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewUser;
import com.mathiaslohne.bbdebet2.gui.modelwrappers.ViewUserVerbose;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class UserList implements Iterable<User>, Exportable, Listable<ViewUser> {

    private LinkedList<User> list;

    private File defaultSave;

    /**
     * Creates an empty user list
     */
    public UserList(String savePath) {
        list = new LinkedList<>();
        defaultSave = new File(savePath);
    }


    /**
     * Reads given file and initializes a user list from the file
     *
     * @param userlist file to read from
     *
     * @throws IOException          if file could not be found/read properly
     * @throws ErrorInFileException if there is an error in the file (file is on an incorrect format)
     */
    public UserList(File userlist) throws IOException, ErrorInFileException {
        resetToFile(userlist);
        defaultSave = userlist;
    }


    private User parseLine(String[] line) throws Exception {
        User temp = new User(line[1], line[2], Integer.parseInt(line[0].replaceAll("\\s+", "")));

        // add the money to the user
        temp.addBalance(Double.parseDouble(line[3]));

        // read whether user has accepted eula or not. try/catch for legacy reasons
        try {
            if (Boolean.parseBoolean(line[4])) temp.acceptEula();
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // Ignore exception. EULA attribute will default to false.
        }

        return temp;
    }


    public void resetToFile(File userlist) throws IOException, ErrorInFileException {
        // start on line 1 (0 for programmers, 1 for humans). this is for the error
        // message generated in the catch-es below.
        int lineNum = 1;

        try {
            // create empty list
            list = new LinkedList<>();

            // create file reader
            Scanner sc = new Scanner(userlist);

            // read first line, and update User's ID-counter
            User.setCounter(Integer.parseInt(sc.nextLine()));

            // update line num
            lineNum++;

            // read rest of file as csv
            while (sc.hasNextLine()) {
                // read line, split on commas
                String[] line = sc.nextLine().split(",");

                // skip empty lines
                if (line.length == 0) continue;

                // create new user from line
                User temp = parseLine(line);

                // add user to list and update line num
                list.add(temp);
                lineNum++;
            }

            sc.close();
        } catch (NumberFormatException e) {
            throw new ErrorInFileException("Feil i inputfil: Linje " + lineNum + " i " + userlist + " inneholder et element av feil type.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ErrorInFileException("Feil i inputfil: Linje " + lineNum + " i " + userlist + " inneholder for få elementer.");
        } catch (NoSuchElementException e) {
            throw new ErrorInFileException("Feil i inputfil: " + userlist + " inneholder for få linjer.");
        } catch (Exception e) {
            throw new ErrorInFileException("Uventet feil i input: " + e.getMessage());
        }

        list.sort(Comparator.comparingInt(User::getId));
    }


    /**
     * saves file as a userlist file (described in fileformat definitions)
     *
     * @param file           where to store file
     * @param includeCounter wether to include total number of users on first line or not
     *
     * @throws IOException if file could not be created for some reason
     */
    public void saveFile(File file, boolean includeCounter) throws IOException {
        if (file.isDirectory()) {
            saveFile(new File(file.getAbsolutePath() + "/userlist.csv"), false);
            return;
        }

        // output stream
        PrintWriter pw = new PrintWriter(file);

        // print User's ID-counter
        if (includeCounter) pw.println(User.getCounter());

        // print all users as csv
        for (User u : list) {
            pw.println(u.getId() + "," + u.getUserName() + "," + u.getMail() + "," + u.getBalance() + "," + u.hasAcceptedEula());
        }

        // close stream
        pw.close();
    }


    /**
     * saves file as a userlist file (described in fileformat definitions)
     *
     * @param file where to store file
     *
     * @throws IOException if file could not be created for some reason
     */
    public void saveFile(File file) throws IOException {
        saveFile(file, false);
    }


    /**
     * saves file as a userlist file (described in fileformat definitions)
     *
     * @param filepath where to store file (relative or absolute path)
     *
     * @throws IOException if file could not be created for some reason
     */
    public void saveFile(String filepath) throws IOException {
        saveFile(new File(filepath));
    }


    /**
     * saves file as a userlist file (described in fileformat definitions)
     *
     * @param filepath       where to store file (relative or absolute path)
     * @param includeCounter wether to include total number of users on first line or not
     *
     * @throws IOException if file could not be created for some reason
     */
    public void saveFile(String filepath, boolean includeCounter) throws IOException {
        saveFile(new File(filepath), includeCounter);
    }


    @Override
    public void saveFile() throws IOException {
        saveFile(defaultSave, true);
    }


    /**
     * adds user to list
     *
     * @param user user to add
     */
    public void add(User user) {
        list.add(user);
    }


    /**
     * check if user exists in list
     *
     * @param user user to search for
     *
     * @return if user exists in list
     */
    public boolean contains(User user) {
        return list.contains(user);
    }


    /**
     * searches for a user with given name in the list
     *
     * @param userName name to search for
     *
     * @return if there is a user with given name or not
     */
    public boolean contains(String userName) {
        return find(userName) != null;
    }


    /**
     * deletes user from list
     *
     * @param user user to remove
     */
    public void remove(User user) {
        list.remove(user);
    }


    /**
     * search for a user in the list by username
     *
     * @param userName name of user to search for
     *
     * @return user with given name. null if there are no users with given name
     */
    public User find(String userName) {
        // loop through list
        for (User u : list) {

            // return u if match
            if (u.getUserName().equalsIgnoreCase(userName)) return u;
        }

        // return null if there are no matches
        return null;
    }


    /**
     * reuturns iterator, makes list iterable
     *
     * @return iterator for list
     */
    public Iterator<User> iterator() {

        // just wrap the iterator in linkedlist
        return list.iterator();
    }


    /**
     * returns list as an observable list
     *
     * @return userlist as an obserable list of usernames
     */
    public ObservableList<ViewUser> toObservableList() {
        ArrayList<ViewUser> listOfPeople = new ArrayList<>();

        for (User u : list) {
            listOfPeople.add(new ViewUser(u));
        }

        return FXCollections.observableArrayList(listOfPeople);
    }


    /**
     * returns list as an observable list
     *
     * @return userlist as an obserable list of usernames
     */
    public ObservableList<ViewUser> toObservableList(boolean verbose) {
        if (!verbose) return toObservableList();

        ArrayList<ViewUser> listOfPeople = new ArrayList<>();

        for (User u : list) {
            listOfPeople.add(new ViewUserVerbose(u));
        }

        return FXCollections.observableArrayList(listOfPeople);
    }


    public List<ViewUser> toList() {
        LinkedList<ViewUser> viewList = new LinkedList<>();

        for (User u : list) {
            viewList.add(new ViewUser(u));
        }

        return viewList;
    }


    public double getTotalBalance() {
        double totalBalance = 0;

        for (User u : list) {
            totalBalance += u.getBalance();
        }

        return totalBalance;
    }


    public double getTotalDebt() {
        double totalBalance = 0;

        for (User u : list) {
            if (u.getBalance() < 0) totalBalance += u.getBalance();
        }

        return -totalBalance;
    }


    public void reset() {
        list = new LinkedList<>();
        User.setCounter(0);
    }
}
