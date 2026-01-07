import { Component, computed, Signal, signal, WritableSignal } from '@angular/core';
import { ServizioSibank } from '../../services/bk.service';
import { BkService } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { DomSanitizer } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

enum SortMode {
  Abi_ASC,
  Abi_DESC,
  Targa_ASC,
  Targa_DESC,
  Ambiente_ASC,
  Ambiente_DESC,
  Servizio_ASC,
  Servizio_DESC,
  Porta_ASC,
  Porta_DESC,
  Programma_ASC,
  Programma_DESC,
  undefined
}

@Component({
  selector: 'app-servizi-sibank-manager',
  imports: [CommonModule, FormsModule],
  templateUrl: './servizi-sibank-manager.html',
  styleUrl: './servizi-sibank-manager.css',
})

export class ServiziSibankManager {

  m_servizi_list_all: WritableSignal<ServizioSibank[]> = signal([]);

  m_sort_mode: SortMode = SortMode.undefined;
  m_filter_abi: WritableSignal<string> = signal('');
  m_filter_targa: WritableSignal<string> = signal('');
  m_filter_ambiente: WritableSignal<string> = signal('');
  m_filter_servizio: WritableSignal<string> = signal('');
  m_filter_porta: WritableSignal<string> = signal('');
  m_filter_abilitato: WritableSignal<string> = signal('');
  m_servizi_list: Signal<ServizioSibank[]> = computed(() => {
    const filteredList = this.m_servizi_list_all().slice();
    var i = filteredList.length;

    while (i--) {
      var element: ServizioSibank = filteredList[i];
      if (this.m_filter_abi().length > 0 && element.abi.indexOf(this.m_filter_abi()) < 0) {
        filteredList.splice(i, 1);
        continue;
      }

      if (this.m_filter_targa().length > 0 && element.targa.indexOf(this.m_filter_targa().toUpperCase()) < 0) {
        filteredList.splice(i, 1);
        continue;
      }

      if (this.m_filter_ambiente().length > 0 && element.ambiente.indexOf(this.m_filter_ambiente().toUpperCase()) < 0) {
        filteredList.splice(i, 1);
        continue;
      }

      if (this.m_filter_servizio().length > 0 && element.name.indexOf(this.m_filter_servizio().toUpperCase()) < 0) {
        filteredList.splice(i, 1);
        continue;
      }

      if (this.m_filter_porta().length > 0 && element.port.indexOf(this.m_filter_porta().toUpperCase()) < 0) {
        filteredList.splice(i, 1);
        continue;
      }

      if (this.m_filter_abilitato().length > 0 && element.enabled.indexOf(this.m_filter_abilitato().toUpperCase()) < 0) {
        filteredList.splice(i, 1);
        continue;
      }

    }
    return filteredList;
  });

  constructor(private bkService: BkService, private message_service: MessageHelperService, private sanitizer: DomSanitizer) {
    this.getServiziSibank();
  }

  getServiziSibank() {
    console.log('[0]-getServiziSibank');
    this.bkService.getServiziSibank().subscribe(
      data => {
        console.log(data);
        this.m_servizi_list_all.set(data);
        this.sort('targa');
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta');
      }
    );
  }

  isCurrentSortASC(): boolean {
    if (this.m_sort_mode === SortMode.Abi_ASC
      || this.m_sort_mode === SortMode.Ambiente_ASC
      || this.m_sort_mode === SortMode.Porta_ASC
      || this.m_sort_mode === SortMode.Programma_ASC
      || this.m_sort_mode === SortMode.Servizio_ASC
      || this.m_sort_mode === SortMode.Targa_ASC
    )
      return true;
    else
      return false;
  }

  sort(sortCol: string) {
    console.log('sort', sortCol);
    if (sortCol === 'abi') {
      if (this.m_sort_mode === SortMode.Abi_ASC) {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.abi + a.targa + a.ambiente > b.abi + b.targa + b.ambiente ? -1 : 1)));
        this.m_sort_mode = SortMode.Abi_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.abi + a.targa + a.ambiente < b.abi + b.targa + b.ambiente ? -1 : 1)));
        this.m_sort_mode = SortMode.Abi_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'servizio') {
      if (this.m_sort_mode === SortMode.Servizio_ASC) {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.name > b.name ? -1 : 1)));
        this.m_sort_mode = SortMode.Servizio_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.name < b.name ? -1 : 1)));
        this.m_sort_mode = SortMode.Servizio_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'targa') {
      if (this.m_sort_mode === SortMode.Targa_ASC) {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.targa + a.ambiente > b.targa + b.ambiente ? -1 : 1)));
        this.m_sort_mode = SortMode.Targa_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.targa + a.ambiente < b.targa + b.ambiente ? -1 : 1)));
        this.m_sort_mode = SortMode.Targa_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'ambiente') {
      if (this.m_sort_mode === SortMode.Ambiente_ASC) {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.ambiente > b.ambiente ? -1 : 1)));
        this.m_sort_mode = SortMode.Ambiente_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.ambiente < b.ambiente ? -1 : 1)));
        this.m_sort_mode = SortMode.Ambiente_ASC; // l'ordinamento fatto e' crescente
      }
    }
    else if (sortCol === 'porta') {
      if (this.m_sort_mode === SortMode.Porta_ASC) {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.port > b.port ? -1 : 1)));
        this.m_sort_mode = SortMode.Porta_DESC; // l'ordinamento fatto e' decrescente
      }
      else {
        this.m_servizi_list_all.update((values) => values.sort((a, b) => (a.port < b.port ? -1 : 1)));
        this.m_sort_mode = SortMode.Porta_ASC; // l'ordinamento fatto e' crescente
      }

    }
    this.m_servizi_list_all.set(this.m_servizi_list_all().slice()); // forza il ricalcolo della lista
  }

}
