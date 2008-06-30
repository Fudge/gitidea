package com.aspiro.git;

import com.intellij.openapi.editor.EditorGutterAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.annotate.AnnotationListener;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.util.text.SyncDateFormat;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Ported for gitidea from SvnFileAnnotation: Copyright 2000-2005 JetBrains s.r.o.
 * by Brett Sealey.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class GitFileAnnotation implements FileAnnotation {
   private final StringBuffer myContentBuffer = new StringBuffer();
   private final List<LineInfo> myLineInfos = new ArrayList<LineInfo>();
   private static final SyncDateFormat DATE_FORMAT = new SyncDateFormat(SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT));

   private final Project project;
   private final List<AnnotationListener> myListeners = new ArrayList<AnnotationListener>();
   private final Map<VcsRevisionNumber, VcsFileRevision> myRevisionMap = new HashMap<VcsRevisionNumber, VcsFileRevision>();

   private final LineAnnotationAspect DATE_ASPECT = new LineAnnotationAspect() {
      public String getValue(int lineNumber) {
         if (myLineInfos.size() <= lineNumber || lineNumber < 0) {
            return "";
         } else {
            return DATE_FORMAT.format(myLineInfos.get(lineNumber).getDate());
         }
      }
   };

   private final LineAnnotationAspect REVISION_ASPECT = new RevisionAnnotationAspect();

   private final LineAnnotationAspect AUTHOR_ASPECT = new LineAnnotationAspect() {
      public String getValue(int lineNumber) {
         if (myLineInfos.size() <= lineNumber || lineNumber < 0) {
            return "";
         } else {
            return myLineInfos.get(lineNumber).getAuthor();
         }
      }
   };

   public void addLogEntries(List<VcsFileRevision> revisions) {
      for (VcsFileRevision vcsFileRevision : revisions) {
         myRevisionMap.put(vcsFileRevision.getRevisionNumber(), vcsFileRevision);
      }
   }

   static class LineInfo {
      private final Date myDate;
      private final GitRevisionNumber myRevision;
      private final String myAuthor;

      public LineInfo(final Date date, final GitRevisionNumber revision, final String author) {
         myDate = date;
         myRevision = revision;
         myAuthor = author;
      }

      public Date getDate() {
         return myDate;
      }

      public GitRevisionNumber getRevision() {
         return myRevision;
      }

      public String getAuthor() {
         return myAuthor;
      }
   }


   public GitFileAnnotation(final Project project) {
      this.project = project;
   }

   public void addListener(AnnotationListener listener) {
      myListeners.add(listener);
   }

   public void removeListener(AnnotationListener listener) {
      myListeners.remove(listener);
   }

   public void dispose() {
   }

   public LineAnnotationAspect[] getAspects() {
      return new LineAnnotationAspect[]{REVISION_ASPECT, DATE_ASPECT, AUTHOR_ASPECT};
   }

   public String getToolTip(final int lineNumber) {
      if (myLineInfos.size() <= lineNumber || lineNumber < 0) {
         return "";
      }
      final LineInfo info = myLineInfos.get(lineNumber);
      VcsFileRevision fileRevision = myRevisionMap.get(info.getRevision());
      if (fileRevision != null) {
         return "commit " + info.getRevision().asString() + "\n"
               + "Author: " + fileRevision.getAuthor() + "\n"
               + "Date: " + fileRevision.getRevisionDate() + "\n"
               + fileRevision.getCommitMessage();
      } else {
         return "";
      }
   }

   public String getAnnotatedContent() {
      return myContentBuffer.toString();
   }

   public void appendLineInfo(final Date date, final GitRevisionNumber revision, final String author, final String line, final long lineNumber) throws VcsException {
      int expectedLineNo = myLineInfos.size() + 1;
      if (lineNumber != expectedLineNo) {
         throw new VcsException("Adding for info for line " + lineNumber + " but we are expecting it to be for " + expectedLineNo);
      }
      myLineInfos.add(new LineInfo(date, revision, author));
      myContentBuffer.append(line);
      myContentBuffer.append("\n");
   }

   private class RevisionAnnotationAspect implements LineAnnotationAspect, EditorGutterAction {
      public String getValue(int lineNumber) {
         if (myLineInfos.size() <= lineNumber || lineNumber < 0) {
            return "";
         } else {
            return String.valueOf(myLineInfos.get(lineNumber).getRevision().getShortRev());
         }
      }

      public Cursor getCursor(final int lineNum) {
         return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
      }

      public void doAction(int lineNum) {
         if (lineNum >= 0 && lineNum < myLineInfos.size()) {
            final LineInfo info = myLineInfos.get(lineNum);
            VcsFileRevision fileRevision = myRevisionMap.get(info.getRevision());
            if (fileRevision != null) {
               System.out.println("GitFileAnnotation$RevisionAnnotationAspect.doAction");
//TODO?                    ShowAllSubmittedFilesAction.showSubmittedFiles(project, fileRevision);
            }
         }
      }
   }
}
