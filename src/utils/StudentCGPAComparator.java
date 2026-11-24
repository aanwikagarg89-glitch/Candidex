package utils;

import model.Student;

import java.util.Comparator;

/**
 * Comparator to sort students in descending order of CGPA.
 * If CGPAs are equal, sorts alphabetically by roll number.
 * Demonstrates Comparator interface and double comparison.
 */
public class StudentCGPAComparator implements Comparator<Student> {

    @Override
    public int compare(Student s1, Student s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;

        double cgpa1 = (s1.getAcademicRecord() != null) ? s1.getAcademicRecord().getCgpa() : 0.0;
        double cgpa2 = (s2.getAcademicRecord() != null) ? s2.getAcademicRecord().getCgpa() : 0.0;

        // Descending order of CGPA
        int cgpaComp = Double.compare(cgpa2, cgpa1);
        if (cgpaComp != 0) {
            return cgpaComp;
        }

        // Secondary sort: Roll Number ascending
        String roll1 = s1.getRollNumber() != null ? s1.getRollNumber() : "";
        String roll2 = s2.getRollNumber() != null ? s2.getRollNumber() : "";
        return roll1.compareToIgnoreCase(roll2);
    }
}
